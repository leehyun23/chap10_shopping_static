package com.javalab.boot.entity;

import lombok.*;

import javax.persistence.*;

@Entity @Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "category_Nm")
    private String name;


}
