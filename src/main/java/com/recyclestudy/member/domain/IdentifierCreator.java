package com.recyclestudy.member.domain;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdentifierCreator {

    public static String create() {
        return UUID.randomUUID().toString();
    }
}
