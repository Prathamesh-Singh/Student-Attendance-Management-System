package edu.pesu.sams.repository;
import edu.pesu.sams.entity.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface AppSettingRepository extends JpaRepository<AppSetting,Long> {
 Optional<AppSetting> findByKey(String key);
}
