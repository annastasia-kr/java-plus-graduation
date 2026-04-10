package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "similarities")
public class EventSimilarity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_a")
    private Long eventA;

    @Column(name = "event_b")
    private Long eventB;

    @Column(name = "similarity")
    private double similarity;

}
