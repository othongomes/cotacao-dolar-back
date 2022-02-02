package shx.cotacaodolar.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import shx.cotacaodolar.model.Moeda;



@Repository
public interface MoedaRepository extends JpaRepository<Moeda, Long> {
   @Query("select moeda from Moeda moeda where data = :dataString")
   Moeda findByDataString(@Param("dataString") String dataString );
}
