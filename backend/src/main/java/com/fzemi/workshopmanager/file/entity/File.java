package com.fzemi.workshopmanager.file.entity;

import com.fzemi.workshopmanager.file.config.FileTags;
import com.fzemi.workshopmanager.repair.entity.Repair;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String contentType;

    private String filePath;

    @Column(nullable = false)
    private String fileUrl;

    private Long size;

    @Convert(converter = FileTagsConverter.class)
    private List<FileTags> tags;

    @ManyToOne
    @JoinColumn(name = "repair_id")
    private Repair repair;
}
