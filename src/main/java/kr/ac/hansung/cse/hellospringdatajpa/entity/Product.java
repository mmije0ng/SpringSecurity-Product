package kr.ac.hansung.cse.hellospringdatajpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "상품 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "브랜드는 필수입니다.")
    private String brand;

    @NotBlank(message = "제조국은 필수입니다.")
    private String madeIn;

    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Double price;

    public Product(String name, String brand, String madeIn, double price) {
        this.name = name;
        this.brand = brand;
        this.madeIn = madeIn;
        this.price = price;
    }
}