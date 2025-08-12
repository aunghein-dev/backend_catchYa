package com.catch_ya_group.catch_ya.modal.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "user_loca")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoca {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long userLocaId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(columnDefinition = "geography(Point,4326)", nullable = false)
    @JdbcTypeCode(SqlTypes.GEOGRAPHY)
    private Point location;
}
