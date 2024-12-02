package com.credaegis.backend.repository;

import com.credaegis.backend.entity.Cluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface  ClusterRepository extends JpaRepository<Cluster,String> {

        @Modifying
        @Query("UPDATE Cluster c SET c.deactivated = true WHERE c.id = :id ")
        void deactivateCluster(@Param("id") String clusterId);

        @Modifying
        @Query("UPDATE  Cluster c SET c.deactivated = false WHERE c.id = :id ")
        void activateCluster(@Param("id") String clusterId);

        @Modifying
        @Query("UPDATE Cluster c SET c.name = :name WHERE c.id = :id")
        void renameCluster(@Param("id") String clusterId, @Param("name") String newName);

        @Modifying
        @Query("UPDATE Cluster c SET c.admin.id = :adminId WHERE c.id = :id")
        void changeAdmin(@Param("adminId") String newAdminId, @Param("id") String clusterId);

        @Modifying
        @Query("UPDATE Cluster c SET c.locked = true WHERE c.id = :id")
        void lockPermissions(@Param("id") String clusterId);

        @Modifying
        @Query("UPDATE Cluster c SET c.locked = false WHERE c.id = :id")
        void unlockPermissions(@Param("id") String clusterId);

}
