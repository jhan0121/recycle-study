package com.recyclestudy.common;

import com.recyclestudy.member.domain.Device;
import com.recyclestudy.member.domain.Member;
import java.lang.reflect.Field;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BaseEntityTest {

    @Test
    @DisplayName("서로 다른 타입이면 id가 같아도 동등하지 않다")
    void fixedImplementation_differentTypes_sameId_shouldNotBeEqual() throws Exception {
        // Given: Member와 Device 인스턴스를 생성하고, 둘 다 id=1로 설정
        final Member member = createMemberWithId(1L);
        final Device device = createDeviceWithId(1L);

        // When
        final boolean result = member.equals(device);

        // Then
        assertThat(result).isFalse();
        assertThat(device.equals(member)).isFalse();
    }

    @Test
    @DisplayName("같은 타입이고 같은 id면 동등하다")
    void currentImplementation_sameType_sameId_shouldBeEqual() throws Exception {
        // Given: 같은 타입의 인스턴스 2개, 같은 id
        final Member member1 = createMemberWithId(1L);
        final Member member2 = createMemberWithId(1L);

        // When & Then: 같은 타입이므로 동등해야 함
        assertThat(member1.equals(member2)).isTrue();
    }

    @Test
    @DisplayName("같은 타입이지만 다른 id면 동등하지 않다")
    void currentImplementation_sameType_differentId_shouldNotBeEqual() throws Exception {
        // Given: 같은 타입의 인스턴스 2개, 다른 id
        final Member member1 = createMemberWithId(1L);
        final Member member2 = createMemberWithId(2L);

        // When & Then: id가 다르므로 동등하지 않아야 함
        assertThat(member1.equals(member2)).isFalse();
    }

    @Test
    @DisplayName("동등한 객체는 같은 hashCode를 가져야 한다")
    void hashCodeContract_equalObjects_sameHashCode() throws Exception {
        // Given: 같은 타입, 같은 id
        final Member member1 = createMemberWithId(1L);
        final Member member2 = createMemberWithId(1L);

        // When: equals는 true
        assertThat(member1.equals(member2)).isTrue();

        // Then: hashCode도 같아야 함 (계약 준수)
        assertThat(member1).hasSameHashCodeAs(member2);
    }

    @Test
    @DisplayName("id가 null인 엔티티의 hashCode() 호출 시 예외가 발생한다")
    void hashCode_withNullId_throwsException() throws Exception {
        // Given: id가 null인 엔티티
        final Member member = createMemberWithId(null);

        // When & Then
        assertThatThrownBy(member::hashCode)
                .isInstanceOf(IllegalStateException.class);
    }

    private Member createMemberWithId(final Long id) throws Exception {
        final var constructor = Member.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final Member member = constructor.newInstance();
        if (id != null) {
            setId(member, id);
        }
        return member;
    }

    private Device createDeviceWithId(final Long id) throws Exception {
        final var constructor = Device.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final Device device = constructor.newInstance();
        if (id != null) {
            setId(device, id);
        }
        return device;
    }

    private void setId(final Object entity, final Long id) throws Exception {
        final Field idField = entity.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
    }
}
