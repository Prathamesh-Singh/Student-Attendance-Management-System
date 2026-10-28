package edu.pesu.sams.controller;

import edu.pesu.sams.entity.AuditLog;
import edu.pesu.sams.repository.AuditLogRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.*;

@RestController @RequestMapping("/api/v1/audit-log") @PreAuthorize("hasRole('ADMIN')")
public class AuditController {
 private final AuditLogRepository audits;
 public AuditController(AuditLogRepository audits){this.audits=audits;}
 @GetMapping public List<Map<String,Object>> list(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to,@RequestParam(required=false) String userEmail,@RequestParam(required=false) String action){
   Specification<AuditLog> spec=(root,q,cb)->{List<Predicate> p=new ArrayList<>();if(from!=null)p.add(cb.greaterThanOrEqualTo(root.get("createdAt"),from.atStartOfDay()));if(to!=null)p.add(cb.lessThan(root.get("createdAt"),to.plusDays(1).atStartOfDay()));if(userEmail!=null&&!userEmail.isBlank())p.add(cb.like(cb.lower(root.get("actorEmail")),"%"+userEmail.toLowerCase(Locale.ROOT)+"%"));if(action!=null&&!action.isBlank())p.add(cb.equal(root.get("action"),action));return cb.and(p.toArray(Predicate[]::new));};
   return audits.findAll(spec,Sort.by(Sort.Direction.DESC,"createdAt")).stream().limit(500).map(a->Map.<String,Object>of("id",a.getId(),"actorEmail",a.getActorEmail()==null?"":a.getActorEmail(),"action",a.getAction(),"targetType",a.getTargetType()==null?"":a.getTargetType(),"targetId",a.getTargetId()==null?"":a.getTargetId(),"oldValue",a.getOldValue()==null?"":a.getOldValue(),"newValue",a.getNewValue()==null?"":a.getNewValue(),"reason",a.getReason()==null?"":a.getReason(),"sourceIp",a.getSourceIp()==null?"":a.getSourceIp(),"createdAt",a.getCreatedAt().toString())).toList();
 }
}
