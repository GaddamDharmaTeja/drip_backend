package com.dripgrow.api.config;

import com.dripgrow.api.model.AdminUser;
import com.dripgrow.api.model.IrrigationPlan;
import com.dripgrow.api.model.ProjectCase;
import com.dripgrow.api.model.ProjectPlan;
import com.dripgrow.api.repository.AdminUserRepository;
import com.dripgrow.api.repository.IrrigationPlanRepository;
import com.dripgrow.api.repository.ProjectCaseRepository;
import com.dripgrow.api.repository.ProjectPlanRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class AdminBootstrap implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final AdminUserRepository adminUserRepository;
    private final IrrigationPlanRepository irrigationPlanRepository;
    private final ProjectPlanRepository projectPlanRepository;
    private final ProjectCaseRepository projectCaseRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;
    private final boolean seedEnabled;

    public AdminBootstrap(
            AdminUserRepository adminUserRepository,
            IrrigationPlanRepository irrigationPlanRepository,
            ProjectPlanRepository projectPlanRepository,
            ProjectCaseRepository projectCaseRepository,
            PasswordEncoder passwordEncoder,
            @Value("${dripgrow.admin.username}") String adminUsername,
            @Value("${dripgrow.admin.password}") String adminPassword,
            @Value("${dripgrow.seed.enabled:true}") boolean seedEnabled
    ) {
        this.adminUserRepository = adminUserRepository;
        this.irrigationPlanRepository = irrigationPlanRepository;
        this.projectPlanRepository = projectPlanRepository;
        this.projectCaseRepository = projectCaseRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.seedEnabled = seedEnabled;
    }

    @Override
    public void run(String... args) {
        if (!adminUserRepository.existsByUsername(adminUsername)) {
            AdminUser user = new AdminUser();
            user.setUsername(adminUsername);
            user.setPasswordHash(passwordEncoder.encode(adminPassword));
            user.setRole("ADMIN");
            adminUserRepository.save(user);
            log.info("Seeded admin user '{}'", adminUsername);
        }

        if (!seedEnabled) {
            return;
        }

        if (irrigationPlanRepository.count() == 0) {
            IrrigationPlan p1 = new IrrigationPlan();
            p1.setName("1 Acre Vegetable Plan");
            p1.setSlug("1-acre-vegetable");
            p1.setCropTypes(List.of("Vegetables"));
            p1.setFarmSize("1 Acre");
            p1.setPrice(12500);
            p1.setDescription("Complete drip package for vegetable beds.");
            p1.setActive(true);
            IrrigationPlan p2 = new IrrigationPlan();
            p2.setName("Orchard Starter Plan");
            p2.setSlug("orchard-starter");
            p2.setCropTypes(List.of("Fruits", "Orchard"));
            p2.setFarmSize("2 Acre");
            p2.setPrice(32500);
            p2.setDescription("Ring drip loops for fruit orchards.");
            p2.setActive(true);
            irrigationPlanRepository.saveAll(List.of(p1, p2));
            log.info("Seeded irrigation plans");
        }

        if (projectPlanRepository.count() == 0) {
            ProjectPlan plan = new ProjectPlan();
            plan.setName("End-to-End Farm Setup");
            plan.setSlug("end-to-end-farm-setup");
            plan.setSummary("Survey, design, supply, install, and handover.");
            plan.setSteps(List.of("Site Survey", "Design", "Supply", "Installation", "Maintenance"));
            plan.setBasePrice(45000);
            plan.setDurationDays(21);
            plan.setActive(true);
            projectPlanRepository.save(plan);
            log.info("Seeded project plans");
        }

        if (projectCaseRepository.count() == 0) {
            ProjectCase c1 = new ProjectCase();
            c1.setTitle("Vegetable Cluster Upgrade");
            c1.setLocation("Nashik, Maharashtra");
            c1.setCrop("Tomato & Capsicum");
            c1.setArea("4 acres");
            c1.setImage("https://images.unsplash.com/photo-1625246333195-78d9c38ad449?auto=format&fit=crop&w=900&q=80");
            c1.setSummary("Site survey to maintenance package with fertigation injectors.");
            c1.setPublished(true);
            ProjectCase c2 = new ProjectCase();
            c2.setTitle("Mango Orchard Rings");
            c2.setLocation("Junagadh, Gujarat");
            c2.setCrop("Mango");
            c2.setArea("12 acres");
            c2.setImage("https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=900&q=80");
            c2.setSummary("Tree-ring drip loops with automated zone scheduling.");
            c2.setPublished(true);
            projectCaseRepository.saveAll(List.of(c1, c2));
            log.info("Seeded project cases");
        }
    }
}
