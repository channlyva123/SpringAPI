package com.setec.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.setec.entities.PostProductDAO;
import com.setec.entities.ProductEntity;
import com.setec.entities.PutProductDAO;
import com.setec.repositories.ProductRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/product")
public class ProductController {

	@Autowired
	private ProductRepository productRepositoty;

	@GetMapping
	public Object getAll() {
		var products = productRepositoty.findAll();
		if (products.isEmpty()) {
			return ResponseEntity.status(404).body(Map.of("message", "Product is emty"));
		}
		return products;
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> addProduct(@Valid @ModelAttribute PostProductDAO postProductDAO, BindingResult result)
			throws IOException {

		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body(result.getFieldErrors().stream()
					.map(error -> Map.of("field", error.getField(), "message", error.getDefaultMessage())).toList());
		}

		String uploadDir = new File("myApp/static").getAbsolutePath();
		File dir = new File(uploadDir);
		if (!dir.exists())
			dir.mkdirs();

		MultipartFile file = postProductDAO.getFile();
		String uniqueName = UUID.randomUUID() + "=" + file.getOriginalFilename();
		file.transferTo(Paths.get(uploadDir, uniqueName));

		ProductEntity product = new ProductEntity();
		product.setProduct_name(postProductDAO.getProduct_name());
		product.setPrice(postProductDAO.getPrice());
		product.setQty(postProductDAO.getQty());
		product.setImageUrl("/static/" + uniqueName);

		productRepositoty.save(product);

		return ResponseEntity.ok(Map.of("Message", product));
	}

	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateProduct(@ModelAttribute PutProductDAO putProductDAO) throws Exception {

		var product = productRepositoty.findById(putProductDAO.getId());
		if (product.isPresent()) {

			var updateProduct = product.get();
			updateProduct.setProduct_name(putProductDAO.getProduct_name());
			updateProduct.setPrice(putProductDAO.getPrice());
			updateProduct.setQty(putProductDAO.getQty());

			if (putProductDAO.getFile() != null) {
				String uploadDir = new File("myApp/static").getAbsolutePath();
				File dir = new File(uploadDir);
				if (!dir.exists())
					dir.mkdirs();

				MultipartFile file = putProductDAO.getFile();
				String uniqueName = UUID.randomUUID() + "=" + file.getOriginalFilename();
				String filePath = Paths.get(uploadDir, uniqueName).toString();

				new File("myApp" + updateProduct.getImageUrl()).delete();
				file.transferTo(new File(filePath));
				updateProduct.setImageUrl("/static/" + uniqueName);
			}

			productRepositoty.save(updateProduct);
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(product.get());
		}

		return ResponseEntity.status(404)
				.body(Map.of("message", "Product id = " + putProductDAO.getId() + " not found"));
	}

	@GetMapping({ "{id}", "/id/{id}" })
	public ResponseEntity<?> getById(@PathVariable("id") Integer id) {
		var product = productRepositoty.findById(id);
		if (product.isPresent()) {
			return ResponseEntity.status(200).body(Map.of("message", product));
		}
		return ResponseEntity.status(404).body(Map.of("message", "Product not found."));
	}
	
	@DeleteMapping({ "{id}", "/id/{id}" })
	public ResponseEntity<?> deleteById(@PathVariable("id") Integer id) {
		var product = productRepositoty.findById(id);
		if (product.isPresent()) {
			var deleteProduct = product.get();
			new File("myApp/" + deleteProduct.getImageUrl()).delete();
			productRepositoty.delete(deleteProduct);
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message", "Product Deleted Successfully."));
		}
		return ResponseEntity.status(404).body(Map.of("message", "Product not found."));
	}
}
