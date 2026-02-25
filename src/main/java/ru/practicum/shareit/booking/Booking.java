package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.model.Item;

import java.time.OffsetDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private OffsetDateTime start;

    @Column(nullable = false)
    private OffsetDateTime end;

    @ManyToOne
    @JoinColumn
    private Item item;
}
