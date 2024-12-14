package com.credaegis.backend.repository;

import com.credaegis.backend.dto.EventInfoDTO;
import com.credaegis.backend.entity.Cluster;
import com.credaegis.backend.entity.Event;
import com.credaegis.backend.http.response.custom.EventSearchResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {


    @Query("SELECT e.id AS id ,e.name AS name,e.cluster.id AS clusterId,e.cluster.name AS clusterName FROM Event e WHERE e.name " +
            "LIKE %:eventName% ")
    List<EventSearchResponse> searchEvents(@Param("eventName") String eventName, @Param("organizationId") String organizationId);

    @Modifying
    @Query("UPDATE Event e SET e.deactivated = false WHERE e.id = :id")
    void activateEvent(@Param("id") String id);

    @Modifying
    @Query("UPDATE Event e SET e.deactivated = true WHERE e.id = :id")
    void deactivateEvent(@Param("id") String id);

    @Modifying
    @Query("UPDATE Event e SET e.name = :name WHERE e.id = :id")
    void renameEvent(@Param("name") String name, @Param("id") String id);

    @Modifying
    @Query("UPDATE Event e SET e.name = :name, e.description = :description WHERE e.id = :id")
    void updateEvent(@Param("name") String name, @Param("description") String description, @Param("id") String id);


    @Query("SELECT new com.credaegis.backend.dto.EventInfoDTO(e.id, e.name, e.description, e.deactivated, e.createdOn)" +
            " FROM Event e WHERE e.cluster = :cluster")
    List<EventInfoDTO> getEventInfo(@Param("cluster") Cluster cluster);


    boolean existsByNameAndCluster(String eventName, Cluster cluster);
}
