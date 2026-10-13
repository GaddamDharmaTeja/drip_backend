package com.dripgrow.api.config;

import com.dripgrow.api.model.Product;
import com.dripgrow.api.repository.ProductRepository;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final ProductRepository productRepository;
    private final boolean seedEnabled;

    public DataSeeder(
            ProductRepository productRepository,
            @Value("${dripgrow.seed.enabled:true}") boolean seedEnabled
    ) {
        this.productRepository = productRepository;
        this.seedEnabled = seedEnabled;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled || productRepository.count() > 0) {
            return;
        }
        productRepository.saveAll(catalog());
        log.info("Seeded {} products into MongoDB", productRepository.count());
    }

    private static String img(String id) {
        return "https://images.unsplash.com/" + id + "?auto=format&fit=crop&w=800&q=80";
    }

    private List<Product> catalog() {
        return List.of(
                product(
                        "Vegetable Farm Drip System",
                        "vegetable-farm-drip-system",
                        "Complete Systems",
                        List.of("Vegetables", "Field Crops"),
                        "DripGrow",
                        12500,
                        15900.0,
                        4.8,
                        124,
                        img("photo-1625246333195-78d9c38ad449"),
                        List.of(
                                img("photo-1625246333195-78d9c38ad449"),
                                img("photo-1464226184884-fa280b87c399"),
                                img("photo-1574943320219-553eb213f72d")
                        ),
                        true,
                        true,
                        "Complete acre-ready drip kit for vegetable beds.",
                        "An end-to-end drip irrigation package sized for vegetable farms.",
                        Map.of(
                                "Coverage", "0.5 – 2 acres",
                                "EmitterFlow", "2 LPH / 4 LPH",
                                "Mainline", "63mm HDPE",
                                "Warranty", "2 years"
                        ),
                        List.of("HDPE mainline & submain", "16mm drip laterals", "Filter assembly"),
                        List.of("0.5 Acre", "1 Acre", "2 Acre")
                ),
                product(
                        "Orchard Micro-Sprinkler Kit",
                        "orchard-micro-sprinkler-kit",
                        "Kits",
                        List.of("Fruits", "Orchard"),
                        "AquaRoot",
                        18900,
                        22000.0,
                        4.6,
                        67,
                        img("photo-1500382017468-9049fed747ef"),
                        List.of(img("photo-1500382017468-9049fed747ef")),
                        true,
                        true,
                        "Tree-spacing micro-sprinklers for orchards.",
                        "Designed for mango, citrus, and guava orchards.",
                        Map.of("Coverage", "1 – 5 acres", "Warranty", "2 years"),
                        List.of("Micro-sprinklers", "PE tubing", "Filter unit"),
                        List.of("1 Acre", "2 Acre", "5 Acre")
                ),
                product(
                        "Greenhouse Precision Kit",
                        "greenhouse-precision-kit",
                        "Complete Systems",
                        List.of("Greenhouse", "Vegetables"),
                        "DripGrow",
                        24500,
                        null,
                        4.9,
                        41,
                        img("photo-1585320806297-9794b3e4eeae"),
                        List.of(img("photo-1585320806297-9794b3e4eeae")),
                        false,
                        true,
                        "Pressure-compensated laterals for polyhouses.",
                        "Uniform watering for greenhouse beds with zone valves.",
                        Map.of("Coverage", "500 – 2000 sq.m", "Warranty", "3 years"),
                        List.of("PC drip lines", "Zone valves", "Filter"),
                        List.of("500 sq.m", "1000 sq.m", "2000 sq.m")
                ),
                product(
                        "HDPE Mainline Pipe 63mm",
                        "hdpe-mainline-63mm",
                        "Pipes",
                        List.of("Vegetables", "Fruits", "Field Crops"),
                        "PipeLine Pro",
                        1850,
                        2100.0,
                        4.4,
                        210,
                        img("photo-1581092160562-40aa08e78837"),
                        List.of(img("photo-1581092160562-40aa08e78837")),
                        false,
                        true,
                        "UV-stabilized 63mm HDPE mainline (100m).",
                        "ISI-grade HDPE pipe suitable for farm mainlines.",
                        Map.of("Diameter", "63mm", "Length", "100m"),
                        List.of("100m HDPE coil"),
                        List.of("Standard")
                ),
                product(
                        "PC Inline Emitters Pack",
                        "pc-inline-emitters",
                        "Emitters",
                        List.of("Vegetables", "Greenhouse"),
                        "AquaRoot",
                        890,
                        null,
                        4.7,
                        156,
                        img("photo-1416879595882-3373a0480b5b"),
                        List.of(img("photo-1416879595882-3373a0480b5b")),
                        true,
                        true,
                        "500 pcs pressure-compensating 2 LPH emitters.",
                        "Clog-resistant PC emitters for uniform row irrigation.",
                        Map.of("Flow", "2 LPH", "Count", "500 pcs"),
                        List.of("500 emitters"),
                        List.of("Standard")
                ),
                product(
                        "Disc Filter Assembly 2\"",
                        "disc-filter-2inch",
                        "Filters",
                        List.of("Vegetables", "Fruits", "Field Crops", "Orchard"),
                        "DripGrow",
                        4200,
                        4800.0,
                        4.5,
                        88,
                        img("photo-1621905252507-b35492cc74b4"),
                        List.of(img("photo-1621905252507-b35492cc74b4")),
                        false,
                        true,
                        "2-inch disc filter for borewell source water.",
                        "Easy-flush disc filter that protects emitters from sediment.",
                        Map.of("Size", "2 inch", "Mesh", "120"),
                        List.of("Filter body", "Disc cartridge", "Flush valve"),
                        List.of("Standard")
                ),
                product(
                        "Smart Zone Controller",
                        "smart-zone-controller",
                        "Controllers",
                        List.of("Greenhouse", "Vegetables", "Orchard"),
                        "DripGrow",
                        8990,
                        null,
                        4.6,
                        53,
                        img("photo-1558618666-fcd25c85cd64"),
                        List.of(img("photo-1558618666-fcd25c85cd64")),
                        false,
                        true,
                        "4-zone battery controller with rain delay.",
                        "Schedule irrigation zones from a simple panel.",
                        Map.of("Zones", "4", "Warranty", "1 year"),
                        List.of("Controller", "Mount kit"),
                        List.of("Standard")
                ),
                product(
                        "Starter Kitchen Garden Kit",
                        "starter-kitchen-garden-kit",
                        "Kits",
                        List.of("Vegetables"),
                        "DripGrow",
                        2499,
                        2999.0,
                        4.3,
                        312,
                        img("photo-1591857177580-dc82b9ac4e1e"),
                        List.of(img("photo-1591857177580-dc82b9ac4e1e")),
                        false,
                        true,
                        "Compact drip kit for home / terrace gardens.",
                        "Easy DIY kit covering up to 100 plants.",
                        Map.of("Plants", "Up to 100", "Hose", "15m"),
                        List.of("Drip hose", "Tees & stakes", "Guide"),
                        List.of("Home garden")
                ),
                product(
                        "Field Crop Tape Roll",
                        "field-crop-tape-roll",
                        "Pipes",
                        List.of("Field Crops", "Vegetables"),
                        "PipeLine Pro",
                        3200,
                        null,
                        4.2,
                        94,
                        img("photo-1466695527723-c3b0480f1042"),
                        List.of(img("photo-1466695527723-c3b0480f1042")),
                        false,
                        false,
                        "1000m drip tape, 30cm spacing.",
                        "Economical drip tape for seasonal field crops.",
                        Map.of("Length", "1000m", "Spacing", "30cm"),
                        List.of("1 roll drip tape"),
                        List.of("Standard")
                ),
                product(
                        "Fruit Orchard Complete System",
                        "fruit-orchard-complete-system",
                        "Complete Systems",
                        List.of("Fruits", "Orchard"),
                        "AquaRoot",
                        32500,
                        38000.0,
                        4.7,
                        29,
                        img("photo-1441974231531-c6227db76b6e"),
                        List.of(img("photo-1441974231531-c6227db76b6e")),
                        true,
                        true,
                        "Turnkey orchard irrigation for 2–5 acres.",
                        "Survey-ready package with ring drip loops for each tree.",
                        Map.of("Coverage", "2 – 5 acres", "Warranty", "3 years"),
                        List.of("Mainline set", "Ring loops", "Filters"),
                        List.of("2 Acre", "3 Acre", "5 Acre")
                ),
                product(
                        "Sand Filter Combo",
                        "sand-filter-combo",
                        "Filters",
                        List.of("Field Crops", "Orchard"),
                        "PipeLine Pro",
                        15600,
                        null,
                        4.4,
                        18,
                        img("photo-1504328345606-18bbc8c9d7d1"),
                        List.of(img("photo-1504328345606-18bbc8c9d7d1")),
                        false,
                        true,
                        "Media + screen filter for canal water.",
                        "Primary filtration stack for open water sources.",
                        Map.of("Media", "Silica sand", "Outlet", "2.5 inch"),
                        List.of("Sand filter", "Screen filter", "Manifold"),
                        List.of("Standard")
                ),
                product(
                        "Button Dripper Assortment",
                        "button-dripper-assortment",
                        "Emitters",
                        List.of("Fruits", "Greenhouse"),
                        "DripGrow",
                        650,
                        null,
                        4.1,
                        77,
                        img("photo-1416879595882-3373a0480b5b"),
                        List.of(img("photo-1416879595882-3373a0480b5b")),
                        false,
                        true,
                        "Mixed 2/4/8 LPH button drippers (200 pcs).",
                        "Handy assortment for orchards and nursery bags.",
                        Map.of("Count", "200 pcs", "Flows", "2 / 4 / 8 LPH"),
                        List.of("Button drippers assortment"),
                        List.of("Standard")
                )
        );
    }

    private static Product product(
            String name,
            String slug,
            String category,
            List<String> cropTypes,
            String brand,
            double price,
            Double originalPrice,
            double rating,
            int reviewCount,
            String image,
            List<String> images,
            boolean bestseller,
            boolean inStock,
            String shortDescription,
            String description,
            Map<String, String> specifications,
            List<String> packageIncludes,
            List<String> farmSizes
    ) {
        Product p = new Product();
        p.setName(name);
        p.setSlug(slug);
        p.setCategory(category);
        p.setCropTypes(cropTypes);
        p.setBrand(brand);
        p.setPrice(price);
        p.setOriginalPrice(originalPrice);
        p.setRating(rating);
        p.setReviewCount(reviewCount);
        p.setImage(image);
        p.setImages(images);
        p.setBestseller(bestseller);
        p.setInStock(inStock);
        p.setShortDescription(shortDescription);
        p.setDescription(description);
        p.setSpecifications(specifications);
        p.setPackageIncludes(packageIncludes);
        p.setFarmSizes(farmSizes);
        return p;
    }
}
