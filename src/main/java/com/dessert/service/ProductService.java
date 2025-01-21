package com.dessert.service;

import com.dessert.entity.Product;
import com.dessert.repository.ProductRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
  private final ProductRepository productRepository;
  private final ImageService imageService;

  public Product getProductById(Long id) {
    return productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
  }

  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  public Product saveProduct(Product product, MultipartFile imageFile) {
    if (imageFile != null && !imageFile.isEmpty()) {
      String imageUrl = imageService.saveImage(imageFile);
      product.setImageUrl(imageUrl);
    }
    return productRepository.save(product);
  }

  public void deleteProduct(Long id) {
    Product product = getProductById(id);
    if (product.getImageUrl() != null) {
      imageService.deleteImage(product.getImageUrl());
    }
    productRepository.deleteById(id);
  }

  public void updateProduct(Long id, Product updatedProduct, MultipartFile imageFile) {
    Product existingProduct = getProductById(id);
    if (imageFile != null && !imageFile.isEmpty()) {
      if (existingProduct.getImageUrl() != null) {
        imageService.deleteImage(existingProduct.getImageUrl());
      }
      String imageUrl = imageService.saveImage(imageFile);
      existingProduct.setImageUrl(imageUrl);
    }
    existingProduct.setName(updatedProduct.getName());
    existingProduct.setPrice(updatedProduct.getPrice());
    existingProduct.setDescription(updatedProduct.getDescription());
    productRepository.save(existingProduct);
  }

  @PostConstruct
  public void initializeProducts() {
    if (productRepository.count() == 0) {
      List<Product> products = Arrays.asList(
              new Product(null, "草莓巴斯克蛋糕", new BigInteger("180"), "濃郁的奶香完美融合草莓的果香味，外層微焦酥脆，內餡柔軟濕潤。這款經典甜品將濃厚的口感層次與細緻風味結合，是無法抗拒的完美甜點。", "/images/basqueCake.jpg"),
              new Product(null, "栗子提拉米蘇", new BigInteger("180"), "細膩的栗子泥與濃郁的馬斯卡彭乳酪完美結合，搭配咖啡酒浸潤的蛋糕層，散發溫潤甜美的栗子香氣，兼具經典與創新的口感享受。", "/images/chestnutTiramisu.jpg"),
              new Product(null, "生巧克力", new BigInteger("250"), "入口即化的濃郁巧克力，帶有細膩的乳香與輕微的果香調，質地柔軟順滑，每一口都充滿純粹的巧克力魅力，是極致甜點的代表。", "/images/chocolate.jpg"),
              new Product(null, "可麗露", new BigInteger("380"), "外層烤得酥脆，帶有焦糖香氣，內餡濕潤細膩，散發濃郁奶香。經典法式甜點的簡約與精緻，讓每一口都回味無窮。", "/images/clearo.jpg"),
              new Product(null, "杜拜巧克力", new BigInteger("280"), "內餡特別融入濃郁的開心果流心，咬下時巧克力與香氣四溢的開心果麵包絲完美融合，搭配外層輕脆的口感，內層流心與「咔滋咔滋」的雙重享受，是奢華與創意的完美詮釋。", "/images/dubaiChocolate.jpg"),
              new Product(null, "草莓堅果雪Q餅", new BigInteger("230"), "草莓的清新果香與堅果的脆香相融合，搭配柔軟的餅乾口感，讓人感受到濃郁與清新的雙重美味，是下午茶的絕佳伴侶。", "/images/snowQ.jpg"),
              new Product(null, "經典檸檬塔", new BigInteger("200"), "以手工製作的酥脆塔皮為基底，搭配新鮮萃取的天然檸檬汁調製的細緻檸檬餡，再佐以少量奶油平衡酸度。塔面點綴清新的檸檬皮屑，不僅視覺誘人，口感更是滑順香濃，酸中帶甜，甜而不膩。", "/images/lemonTart.jpg")
              // ... 添加更多產品
      );
      productRepository.saveAll(products);
    }
  }

}