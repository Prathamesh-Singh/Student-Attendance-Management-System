package edu.pesu.sams.entity;
import jakarta.persistence.*;
@Entity @Table(name="app_settings", uniqueConstraints=@UniqueConstraint(name="uk_app_setting_key", columnNames="setting_key"))
public class AppSetting {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="setting_key", nullable=false, length=80) private String key;
    @Column(name="setting_value", nullable=false, length=250) private String value;
    public AppSetting() {} public AppSetting(String key,String value){this.key=key;this.value=value;}
    public Long getId(){return id;} public String getKey(){return key;} public String getValue(){return value;} public void setValue(String v){value=v;}
}
