package com.dripgrow.api.controller;

import com.dripgrow.api.dto.OrderResponse;
import com.dripgrow.api.dto.UpdateOrderRequest;
import com.dripgrow.api.model.IrrigationPlan;
import com.dripgrow.api.model.Product;
import com.dripgrow.api.model.ProjectCase;
import com.dripgrow.api.model.ProjectPlan;
import com.dripgrow.api.model.Quotation;
import com.dripgrow.api.model.ServiceRequest;
import com.dripgrow.api.repository.OrderRepository;
import com.dripgrow.api.repository.ProductRepository;
import com.dripgrow.api.repository.QuotationRepository;
import com.dripgrow.api.repository.ServiceRequestRepository;
import com.dripgrow.api.service.CatalogAdminService;
import com.dripgrow.api.service.OrderService;
import com.dripgrow.api.service.RequestQuotationService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final CatalogAdminService catalogAdminService;
    private final OrderService orderService;
    private final RequestQuotationService requestQuotationService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final QuotationRepository quotationRepository;

    public AdminController(
            CatalogAdminService catalogAdminService,
            OrderService orderService,
            RequestQuotationService requestQuotationService,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            ServiceRequestRepository serviceRequestRepository,
            QuotationRepository quotationRepository
    ) {
        this.catalogAdminService = catalogAdminService;
        this.orderService = orderService;
        this.requestQuotationService = requestQuotationService;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.quotationRepository = quotationRepository;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        long products = productRepository.count();
        long orders = orderRepository.count();
        long newRequests = serviceRequestRepository.countByStatus("New");
        long openQuotes = quotationRepository.countByStatus("Draft")
                + quotationRepository.countByStatus("Sent");
        long outOfStock = productRepository.findAll().stream().filter(p -> !p.isInStock()).count();
        return Map.of(
                "products", products,
                "orders", orders,
                "newRequests", newRequests,
                "openQuotations", openQuotes,
                "outOfStock", outOfStock
        );
    }

    @GetMapping("/products")
    public List<Product> products() {
        return catalogAdminService.listProducts();
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody Product product) {
        product.setId(null);
        return catalogAdminService.saveProduct(product);
    }

    @PutMapping("/products/{id}")
    public Product updateProduct(@PathVariable String id, @RequestBody Product product) {
        return catalogAdminService.updateProduct(id, product);
    }

    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable String id) {
        catalogAdminService.deleteProduct(id);
    }

    @GetMapping("/orders")
    public List<OrderResponse> orders() {
        return orderService.listOrders(null);
    }

    @PatchMapping("/orders/{orderNumber}")
    public OrderResponse updateOrder(
            @PathVariable String orderNumber,
            @RequestBody UpdateOrderRequest body
    ) {
        return orderService.updateOrder(orderNumber, body.getStatus(), body.getAdminNotes());
    }

    @GetMapping("/irrigation-plans")
    public List<IrrigationPlan> irrigationPlans() {
        return catalogAdminService.listIrrigationPlans(false);
    }

    @PostMapping("/irrigation-plans")
    @ResponseStatus(HttpStatus.CREATED)
    public IrrigationPlan createIrrigationPlan(@RequestBody IrrigationPlan plan) {
        plan.setId(null);
        return catalogAdminService.saveIrrigationPlan(plan);
    }

    @PutMapping("/irrigation-plans/{id}")
    public IrrigationPlan updateIrrigationPlan(@PathVariable String id, @RequestBody IrrigationPlan plan) {
        return catalogAdminService.updateIrrigationPlan(id, plan);
    }

    @DeleteMapping("/irrigation-plans/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIrrigationPlan(@PathVariable String id) {
        catalogAdminService.deleteIrrigationPlan(id);
    }

    @GetMapping("/project-plans")
    public List<ProjectPlan> projectPlans() {
        return catalogAdminService.listProjectPlans(false);
    }

    @PostMapping("/project-plans")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectPlan createProjectPlan(@RequestBody ProjectPlan plan) {
        plan.setId(null);
        return catalogAdminService.saveProjectPlan(plan);
    }

    @PutMapping("/project-plans/{id}")
    public ProjectPlan updateProjectPlan(@PathVariable String id, @RequestBody ProjectPlan plan) {
        return catalogAdminService.updateProjectPlan(id, plan);
    }

    @DeleteMapping("/project-plans/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProjectPlan(@PathVariable String id) {
        catalogAdminService.deleteProjectPlan(id);
    }

    @GetMapping("/projects")
    public List<ProjectCase> projects() {
        return catalogAdminService.listProjectCases(false);
    }

    @PostMapping("/projects")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectCase createProject(@RequestBody ProjectCase project) {
        project.setId(null);
        return catalogAdminService.saveProjectCase(project);
    }

    @PutMapping("/projects/{id}")
    public ProjectCase updateProject(@PathVariable String id, @RequestBody ProjectCase project) {
        return catalogAdminService.updateProjectCase(id, project);
    }

    @DeleteMapping("/projects/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable String id) {
        catalogAdminService.deleteProjectCase(id);
    }

    @GetMapping("/requests")
    public List<ServiceRequest> requests() {
        return requestQuotationService.listRequests();
    }

    @PatchMapping("/requests/{id}")
    public ServiceRequest updateRequest(@PathVariable String id, @RequestBody Map<String, String> body) {
        return requestQuotationService.updateRequestStatus(id, body.getOrDefault("status", "InProgress"));
    }

    @PostMapping("/requests/{id}/convert-quote")
    @ResponseStatus(HttpStatus.CREATED)
    public Quotation convertQuote(
            @PathVariable String id,
            @RequestBody Map<String, Object> body
    ) {
        double amount = body.get("amount") instanceof Number n ? n.doubleValue() : 0;
        String notes = body.get("notes") != null ? String.valueOf(body.get("notes")) : null;
        return requestQuotationService.convertRequestToQuotation(id, amount, notes);
    }

    @GetMapping("/quotations")
    public List<Quotation> quotations() {
        return requestQuotationService.listQuotations();
    }

    @PostMapping("/quotations")
    @ResponseStatus(HttpStatus.CREATED)
    public Quotation createQuotation(@RequestBody Quotation quotation) {
        return requestQuotationService.createQuotation(quotation);
    }

    @PatchMapping("/quotations/{id}")
    public Quotation updateQuotation(@PathVariable String id, @RequestBody Quotation quotation) {
        return requestQuotationService.updateQuotation(id, quotation);
    }
}
