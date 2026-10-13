package com.dripgrow.api.service;

import com.dripgrow.api.model.IrrigationPlan;
import com.dripgrow.api.model.Product;
import com.dripgrow.api.model.ProjectCase;
import com.dripgrow.api.model.ProjectPlan;
import com.dripgrow.api.repository.IrrigationPlanRepository;
import com.dripgrow.api.repository.ProductRepository;
import com.dripgrow.api.repository.ProjectCaseRepository;
import com.dripgrow.api.repository.ProjectPlanRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CatalogAdminService {

    private final ProductRepository productRepository;
    private final IrrigationPlanRepository irrigationPlanRepository;
    private final ProjectPlanRepository projectPlanRepository;
    private final ProjectCaseRepository projectCaseRepository;

    public CatalogAdminService(
            ProductRepository productRepository,
            IrrigationPlanRepository irrigationPlanRepository,
            ProjectPlanRepository projectPlanRepository,
            ProjectCaseRepository projectCaseRepository
    ) {
        this.productRepository = productRepository;
        this.irrigationPlanRepository = irrigationPlanRepository;
        this.projectPlanRepository = projectPlanRepository;
        this.projectCaseRepository = projectCaseRepository;
    }

    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(String id, Product incoming) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        incoming.setId(existing.getId());
        return productRepository.save(incoming);
    }

    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        productRepository.deleteById(id);
    }

    public List<IrrigationPlan> listIrrigationPlans(boolean activeOnly) {
        return activeOnly ? irrigationPlanRepository.findByActiveTrue() : irrigationPlanRepository.findAll();
    }

    public IrrigationPlan saveIrrigationPlan(IrrigationPlan plan) {
        return irrigationPlanRepository.save(plan);
    }

    public IrrigationPlan updateIrrigationPlan(String id, IrrigationPlan plan) {
        if (!irrigationPlanRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Irrigation plan not found");
        }
        plan.setId(id);
        return irrigationPlanRepository.save(plan);
    }

    public void deleteIrrigationPlan(String id) {
        irrigationPlanRepository.deleteById(id);
    }

    public List<ProjectPlan> listProjectPlans(boolean activeOnly) {
        return activeOnly ? projectPlanRepository.findByActiveTrue() : projectPlanRepository.findAll();
    }

    public ProjectPlan saveProjectPlan(ProjectPlan plan) {
        return projectPlanRepository.save(plan);
    }

    public ProjectPlan updateProjectPlan(String id, ProjectPlan plan) {
        if (!projectPlanRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project plan not found");
        }
        plan.setId(id);
        return projectPlanRepository.save(plan);
    }

    public void deleteProjectPlan(String id) {
        projectPlanRepository.deleteById(id);
    }

    public List<ProjectCase> listProjectCases(boolean publishedOnly) {
        return publishedOnly ? projectCaseRepository.findByPublishedTrue() : projectCaseRepository.findAll();
    }

    public ProjectCase saveProjectCase(ProjectCase projectCase) {
        return projectCaseRepository.save(projectCase);
    }

    public ProjectCase updateProjectCase(String id, ProjectCase projectCase) {
        if (!projectCaseRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }
        projectCase.setId(id);
        return projectCaseRepository.save(projectCase);
    }

    public void deleteProjectCase(String id) {
        projectCaseRepository.deleteById(id);
    }
}
