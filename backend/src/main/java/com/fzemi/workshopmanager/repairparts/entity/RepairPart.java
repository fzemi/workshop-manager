package com.fzemi.workshopmanager.repairparts.entity;

import com.fzemi.workshopmanager.part.entity.Part;
import com.fzemi.workshopmanager.repair.entity.Repair;
import com.fzemi.workshopmanager.repairparts.config.WorkTypeFormat;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "repair_parts")
public class RepairPart {
    // TODO: In future add orderedFrom field

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "repair_id")
    private Repair repair;

    @ManyToOne
    @JoinColumn(name = "part_id")
    private Part part;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkTypeFormat workType;

    private Integer quantity;
}
