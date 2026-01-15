package com.recyclestudy.common.resolver;

import com.recyclestudy.common.annotation.AuthDevice;
import com.recyclestudy.exception.UnauthorizedException;
import com.recyclestudy.member.domain.Device;
import com.recyclestudy.member.domain.DeviceIdentifier;
import com.recyclestudy.member.repository.DeviceRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class DeviceAuthArgumentResolver implements HandlerMethodArgumentResolver {

    private final DeviceRepository deviceRepository;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthDevice.class);
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) {
        final String headerIdentifier = Optional.ofNullable(webRequest.getHeader("X-device-Id"))
                .orElseThrow(() -> new UnauthorizedException("디바이스 인증 헤더가 누락되었습니다"));

        final DeviceIdentifier identifier = DeviceIdentifier.from(headerIdentifier);
        final Device device = deviceRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new UnauthorizedException("유효하지 않은 디바이스입니다"));

        if (!device.isActive()) {
            throw new UnauthorizedException("인증되지 않은 디바이스입니다");
        }

        return identifier;
    }
}
