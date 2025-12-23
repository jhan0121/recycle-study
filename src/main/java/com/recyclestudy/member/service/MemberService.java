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

    private Member saveNewMember(final Email email) {
        final Optional<Member> memberOptional = memberRepository.findByEmail(email);

        if (memberOptional.isPresent()) {
            return memberOptional.get();
        }

        final Member notSavedMember = Member.withoutId(email);
        return memberRepository.save(notSavedMember);
    }
}
