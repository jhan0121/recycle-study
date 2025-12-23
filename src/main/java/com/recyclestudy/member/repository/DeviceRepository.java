package com.recyclestudy.member.repository;

import com.recyclestudy.member.domain.Device;
import com.recyclestudy.member.domain.DeviceIdentifier;
import com.recyclestudy.member.domain.Email;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    @Query("""
                SELECT d
                FROM Device d
                JOIN FETCH d.member m
                WHERE m.email = :email
            """)
    List<Device> findAllByMemberEmail(@Param("email") Email email);

    Optional<Device> findByIdentifier(DeviceIdentifier deviceIdentifier);
}
