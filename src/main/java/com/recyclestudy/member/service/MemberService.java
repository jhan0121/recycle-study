package com.recyclestudy.member.service;

import com.recyclestudy.exception.BadRequestException;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final DeviceRepository deviceRepository;

    @Transactional
    public MemberSaveOutput saveDevice(final MemberSaveInput input) {
        final Member member = saveNewMember(input.email());
        final DeviceIdentifier deviceIdentifier = DeviceIdentifier.create();

        final Device notSavedDevice = Device.withoutId(member, deviceIdentifier, false);
        final Device device = deviceRepository.save(notSavedDevice);

        return MemberSaveOutput.from(device);
    }

    @Transactional(readOnly = true)
    public MemberFindOutput findAllMemberDevices(final MemberFindInput input) {
        checkExistedMember(input.email());
        checkActiveDevice(input.deviceIdentifier());

        final List<Device> devices = deviceRepository.findAllByMemberEmail(input.email());
        return MemberFindOutput.from(devices);
    }

    @Transactional
    public void authenticateDevice(Email email, DeviceIdentifier deviceIdentifier) {
        checkExistedMember(email);

        final Device device = deviceRepository.findByIdentifier(deviceIdentifier)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 디바이스 아이디입니다: %s"
                        .formatted(deviceIdentifier.getValue())));

        if (device.isActive()) {
            throw new BadRequestException("이미 인증되었습니다");
        }

        device.activate();
    }

    private Member saveNewMember(final Email email) {
        final Optional<Member> memberOptional = memberRepository.findByEmail(email);

        if (memberOptional.isPresent()) {
            return memberOptional.get();
        }

        final Member notSavedMember = Member.withoutId(email);
        return memberRepository.save(notSavedMember);
    }

    private void checkExistedMember(final Email email) {
        if (!memberRepository.existsByEmail(email)) {
            throw new NotFoundException("존재하지 않는 멤버입니다: %s".formatted(email.getValue()));
        }
    }

    private void checkActiveDevice(final DeviceIdentifier deviceIdentifier) {
        final Device device = deviceRepository.findByIdentifier(deviceIdentifier)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 디바이스 아이디입니다: %s"
                        .formatted(deviceIdentifier.getValue())));

        if (!device.isActive()) {
            throw new UnauthorizedException("인증되지 않은 디바이스입니다");
        }
    }
}
