package com.recyclestudy.member.service;

import com.recyclestudy.email.EmailService;
import com.recyclestudy.exception.NotFoundException;
import com.recyclestudy.exception.UnauthorizedException;
import com.recyclestudy.member.domain.Device;
import com.recyclestudy.member.domain.DeviceIdentifier;
import com.recyclestudy.member.domain.Email;
import com.recyclestudy.member.domain.Member;
import com.recyclestudy.member.repository.DeviceRepository;
import com.recyclestudy.member.repository.MemberRepository;
import com.recyclestudy.member.service.input.MemberFindInput;
import com.recyclestudy.member.service.input.MemberSaveInput;
import com.recyclestudy.member.service.output.MemberFindOutput;
import com.recyclestudy.member.service.output.MemberSaveOutput;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    DeviceRepository deviceRepository;

    @Mock
    EmailService emailService;

    @InjectMocks
    MemberService memberService;

    @Test
    @DisplayName("저장되지 않은 이메일일 경우 멤버를 저장 후, 새로운 디바이스 id를 저장한다")
    void saveDevice_newMember() {
        // given
        final MemberSaveInput input = MemberSaveInput.from("new@test.com");
        final Member newMember = Member.withoutId(input.email());
        final Device device = Device.withoutId(newMember, DeviceIdentifier.create(), false);

        given(memberRepository.findByEmail(any(Email.class))).willReturn(Optional.empty());
        given(memberRepository.save(any(Member.class))).willReturn(newMember);
        given(deviceRepository.save(any(Device.class))).willReturn(device);

        // when
        final MemberSaveOutput actual = memberService.saveDevice(input);

        // then
        assertThat(actual.email()).isEqualTo(input.email());
        verify(memberRepository).findByEmail(any(Email.class));
        verify(memberRepository).save(any(Member.class));
        verify(deviceRepository).save(any(Device.class));
        verify(emailService).sendDeviceAuthMail(any(String.class), any(String.class));
    }

    @Test
    @DisplayName("이미 저장된 이메일일 경우 기존 멤버로 새로운 디바이스 id를 저장한다")
    void saveDevice_existedMember() {
        // given
        final MemberSaveInput input = MemberSaveInput.from("existed@test.com");
        final Member existedMember = Member.withoutId(input.email());
        final Device device = Device.withoutId(existedMember, DeviceIdentifier.create(), false);

        given(memberRepository.findByEmail(any(Email.class))).willReturn(Optional.of(existedMember));
        given(deviceRepository.save(any(Device.class))).willReturn(device);

        // when
        final MemberSaveOutput actual = memberService.saveDevice(input);

        // then
        assertThat(actual.email()).isEqualTo(input.email());
        verify(memberRepository).findByEmail(any(Email.class));
        verify(memberRepository, never()).save(any(Member.class));
        verify(deviceRepository).save(any(Device.class));
        verify(emailService).sendDeviceAuthMail(any(String.class), any(String.class));
    }

    @Test
    @DisplayName("대상 이메일을 가진 멤버의 디바이스를 모두 조회한다")
    void findAllMemberDevices() {
        // given
        final String email = "existed@test.com";
        final String identifier = "device-id";
        final MemberFindInput input = MemberFindInput.from(email, identifier);
        final Member existedMember = Member.withoutId(input.email());
        final Device device = Device.withoutId(existedMember, input.deviceIdentifier(), true);

        given(memberRepository.existsByEmail(any(Email.class))).willReturn(true);
        given(deviceRepository.findByIdentifier(any(DeviceIdentifier.class))).willReturn(Optional.of(device));
        given(deviceRepository.findAllByMemberEmail(any(Email.class))).willReturn(List.of(device));

        // when
        final MemberFindOutput actual = memberService.findAllMemberDevices(input);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.elements()).hasSize(1);
            softAssertions.assertThat(actual.elements().getFirst().email()).isEqualTo(input.email());
        });
    }

    @Test
    @DisplayName("대상 이메일을 가진 멤버의 디바이스가 없으면 빈 리스트를 리턴한다")
    void findAllMemberDevices_notExistedDevice() {
        // given
        final String email = "existed@test.com";
        final String identifier = "device-id";
        final MemberFindInput input = MemberFindInput.from(email, identifier);
        final Member existedMember = Member.withoutId(input.email());
        final Device device = Device.withoutId(existedMember, input.deviceIdentifier(), true);

        given(memberRepository.existsByEmail(any(Email.class))).willReturn(true);
        given(deviceRepository.findByIdentifier(any(DeviceIdentifier.class))).willReturn(Optional.of(device));
        given(deviceRepository.findAllByMemberEmail(any(Email.class))).willReturn(List.of());

        // when
        final MemberFindOutput actual = memberService.findAllMemberDevices(input);

        // then
        assertThat(actual.elements()).isEmpty();
    }

    @Test
    @DisplayName("대상 이메일을 가진 멤버가 존재하지 않을 경우 예외를 던진다")
    void throwExceptionWhenNotExistedMemberByEmail() {
        // given
        final String notExistedEmailValue = "notExisted@test.com";
        final String identifier = "device-id";
        final MemberFindInput input = MemberFindInput.from(notExistedEmailValue, identifier);

        given(memberRepository.existsByEmail(any(Email.class))).willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> memberService.findAllMemberDevices(input))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 디바이스 아이디일 경우 예외를 던진다")
    void throwExceptionWhenNotExistedDeviceByIdentifier() {
        // given
        final String email = "existed@test.com";
        final String identifier = "not-existed-device-id";
        final MemberFindInput input = MemberFindInput.from(email, identifier);

        given(memberRepository.existsByEmail(any(Email.class))).willReturn(true);
        given(deviceRepository.findByIdentifier(any(DeviceIdentifier.class))).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> memberService.findAllMemberDevices(input))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("활성화되지 않은 디바이스일 경우 예외를 던진다")
    void throwExceptionWhenNotActiveDevice() {
        // given
        final String email = "existed@test.com";
        final String identifier = "inactive-device-id";
        final MemberFindInput input = MemberFindInput.from(email, identifier);
        final Member existedMember = Member.withoutId(input.email());
        final Device inactiveDevice = Device.withoutId(existedMember, input.deviceIdentifier(), false);

        given(memberRepository.existsByEmail(any(Email.class))).willReturn(true);
        given(deviceRepository.findByIdentifier(any(DeviceIdentifier.class))).willReturn(Optional.of(inactiveDevice));

        // when
        // then
        assertThatThrownBy(() -> memberService.findAllMemberDevices(input))
                .isInstanceOf(UnauthorizedException.class);
    }
}
