-- member
create table member
(
    id          bigint auto_increment primary key,
    email       varchar(255) not null,
    created_at  datetime(6)  not null,
    modified_at datetime(6)  null,
    constraint uk_member_email unique (email)
);

-- device
create table device
(
    id                    bigint auto_increment primary key,
    identifier            varchar(255) not null,
    is_active             bit          not null,
    activation_expires_at datetime(6)  not null,
    member_id             bigint       not null,
    created_at            datetime(6)  not null,
    modified_at           datetime(6)  null,
    constraint uk_device_identifier unique (identifier),
    constraint fk_device_member_id
        foreign key (member_id) references member (id)
);

-- review
create table review
(
    id          bigint auto_increment primary key,
    url         text        not null,
    member_id   bigint      not null,
    created_at  datetime(6) not null,
    modified_at datetime(6) null,
    constraint fk_review_member_id
        foreign key (member_id) references member (id)
);

-- review_cycle
create table review_cycle
(
    id           bigint auto_increment primary key,
    review_id    bigint      not null,
    scheduled_at datetime(6) null,
    created_at   datetime(6) not null,
    modified_at  datetime(6) null,
    constraint fk_review_cycle_review_id
        foreign key (review_id) references review (id)
);

-- notification_history
create table notification_history
(
    id              bigint auto_increment              primary key,
    review_cycle_id bigint                             not null,
    created_at      datetime(6)                        not null,
    modified_at     datetime(6)                        null,
    status          enum ('FAILED', 'PENDING', 'SENT') not null,
    constraint fk_notification_history_review_cycle_id
        foreign key (review_cycle_id) references review_cycle (id)
);
