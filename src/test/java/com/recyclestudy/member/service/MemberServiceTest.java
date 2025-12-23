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
}
