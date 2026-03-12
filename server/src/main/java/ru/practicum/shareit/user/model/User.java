package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"email"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Collection<Item> items = new ArrayList<>();
}
