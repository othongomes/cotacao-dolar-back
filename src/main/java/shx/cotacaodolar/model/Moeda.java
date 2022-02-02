package shx.cotacaodolar.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;



@Entity
@Table(name = "moeda")
public class Moeda implements Serializable{
    
    @NotNull(message = "O preço não pode ser nulo")
    @Min(value = 0, message = "O preço não pode ser negativo")
    @Column(nullable = false, updatable = false)
    public Double preco;
    
    @Id
    @NotNull(message = "A data não pode ser nula")
    @Column(nullable = false, updatable = false, unique = true)
    public String data;
    
    @NotNull(message = "A hora não pode ser nula")
    @Column(nullable = false, updatable = false)
    public String hora;

    public String toString(){
        return preco.toString() + data.toString() + hora;
    }
}