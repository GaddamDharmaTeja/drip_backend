package com.dripgrow.api.config;

import com.dripgrow.api.model.AdminUser;
import com.dripgrow.api.model.AppSettings;
import com.dripgrow.api.model.IrrigationPlan;
import com.dripgrow.api.model.ProjectCase;
import com.dripgrow.api.model.ProjectPlan;
import com.dripgrow.api.repository.AdminUserRepository;
import com.dripgrow.api.repository.AppSettingsRepository;
import com.dripgrow.api.repository.IrrigationPlanRepository;
import com.dripgrow.api.repository.ProjectCaseRepository;
import com.dripgrow.api.repository.ProjectPlanRepository;
import com.dripgrow.api.security.JwtService;
import com.dripgrow.api.service.SiteContactService;
import java.security.SecureRandom;
import java.util.Base64;
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
    private final AppSettingsRepository appSettingsRepository;
    private final IrrigationPlanRepository irrigationPlanRepository;
    private final ProjectPlanRepository projectPlanRepository;
    private final ProjectCaseRepository projectCaseRepository;
    private final SiteContactService siteContactService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String adminUsername;
    private final String adminPassword;
    private final String envJwtSecret;
    private final boolean seedEnabled;

    public AdminBootstrap(
            AdminUserRepository adminUserRepository,
            AppSettingsRepository appSettingsRepository,
            IrrigationPlanRepository irrigationPlanRepository,
            ProjectPlanRepository projectPlanRepository,
            ProjectCaseRepository projectCaseRepository,
            SiteContactService siteContactService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${dripgrow.admin.username}") String adminUsername,
            @Value("${dripgrow.admin.password}") String adminPassword,
            @Value("${dripgrow.jwt.secret}") String envJwtSecret,
            @Value("${dripgrow.seed.enabled:true}") boolean seedEnabled
    ) {
        this.adminUserRepository = adminUserRepository;
        this.appSettingsRepository = appSettingsRepository;
        this.irrigationPlanRepository = irrigationPlanRepository;
        this.projectPlanRepository = projectPlanRepository;
        this.projectCaseRepository = projectCaseRepository;
        this.siteContactService = siteContactService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.envJwtSecret = envJwtSecret;
        this.seedEnabled = seedEnabled;
    }

    @Override
    public void run(String... args) {
        ensureAppSettings();
        siteContactService.ensureDefaults();

        String username = appSettingsRepository.findById(AppSettings.DEFAULT_ID)
                .map(AppSettings::getAdminUsername)
                .filter(s -> s != null && !s.isBlank())
                .orElse(adminUsername);

        String password = appSettingsRepository.findById(AppSettings.DEFAULT_ID)
                .map(AppSettings::getAdminBootstrapPassword)
                .filter(s -> s != null && !s.isBlank())
                .orElse(adminPassword);

        if (!adminUserRepository.existsByUsername(username)) {
            AdminUser user = new AdminUser();
            user.setUsername(username);
            user.setPasswordHash(passwordEncoder.encode(password));
            user.setRole("OWNER");
            adminUserRepository.save(user);
            log.info("Seeded admin user '{}'", username);
        }
        else {
            adminUserRepository.findByUsername(username).ifPresent(user -> {
                if ("ADMIN".equalsIgnoreCase(user.getRole())) { user.setRole("OWNER"); adminUserRepository.save(user); }
            });
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

    private void ensureAppSettings() {
        AppSettings settings = appSettingsRepository.findById(AppSettings.DEFAULT_ID)
                .orElseGet(AppSettings::new);
        boolean changed = false;

        if (settings.getId() == null || settings.getId().isBlank()) {
            settings.setId(AppSettings.DEFAULT_ID);
            changed = true;
        }

        if (settings.getJwtSecret() == null || settings.getJwtSecret().isBlank()) {
            String secret = isPlaceholder(envJwtSecret) ? generateSecret() : envJwtSecret;
            settings.setJwtSecret(secret);
            changed = true;
            log.info("Stored JWT_SECRET in MongoDB collection app_settings");
        }

        if (settings.getAdminUsername() == null || settings.getAdminUsername().isBlank()) {
            settings.setAdminUsername(adminUsername);
            changed = true;
        }

        if (settings.getAdminBootstrapPassword() == null || settings.getAdminBootstrapPassword().isBlank()) {
            settings.setAdminBootstrapPassword(adminPassword);
            changed = true;
        }

        if (changed) {
            appSettingsRepository.save(settings);
        }
        jwtService.reloadSecret();
    }

    private static boolean isPlaceholder(String secret) {
        return secret == null
                || secret.isBlank()
                || secret.contains("change-me")
                || secret.contains("REPLACE_WITH");
    }

    private static String generateSecret() {
        byte[] bytes = new byte[48];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
