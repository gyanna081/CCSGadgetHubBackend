package com.example.GadgetHub.service;

import com.example.GadgetHub.model.Item;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.cloud.storage.BlobInfo;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class ItemService {

    private final Firestore firestore;

    public ItemService() {
        this.firestore = FirestoreClient.getFirestore();
    }

    public Item saveItem(String name, String description, String condition, MultipartFile image) throws IOException {
        try {
            // Create a unique ID for the item
            String itemId = UUID.randomUUID().toString();
            
            // Initialize item data
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("id", itemId);
            itemData.put("name", name);
            itemData.put("description", description);
            itemData.put("condition", condition);
            itemData.put("status", "Available");
            itemData.put("createdAt", LocalDateTime.now().toString());
            
            // Use a reliable placeholder image URL
            String imageUrl = "https://placehold.co/150x150/gray/white?text=Item";
            
            // Handle image upload if provided
            if (image != null && !image.isEmpty()) {
                try {
                    System.out.println("Processing image upload: " + image.getOriginalFilename());
                    
                    // Either use Firebase Storage (if properly configured)
                    // Or use a placeholder image for now
                    imageUrl = "https://placehold.co/150x150/gray/white?text=" + name;
                    
                    /* 
                    // Uncomment this block when Firebase Storage is properly configured
                    // Create a unique filename
                    String filename = UUID.randomUUID() + "_" + image.getOriginalFilename().replace(" ", "_");
                    
                    // Get storage bucket reference
                    StorageClient storageClient = StorageClient.getInstance();
                    
                    // Upload image bytes
                    BlobInfo blobInfo = storageClient.bucket().create(
                        filename, 
                        image.getBytes(), 
                        image.getContentType()
                    );
                    
                    // Get the public download URL
                    String bucketName = storageClient.bucket().getName();
                    
                    // Format the URL in a way that will work for public access
                    imageUrl = String.format(
                        "https://storage.googleapis.com/%s/%s", 
                        bucketName, 
                        filename
                    );
                    */
                    
                    System.out.println("Image URL set to: " + imageUrl);
                } catch (Exception e) {
                    System.err.println("Error handling image: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // Set the image path in the data
            itemData.put("imagePath", imageUrl);
            
            // Save to Firestore
            System.out.println("Saving item to Firestore with ID: " + itemId);
            FirestoreClient.getFirestore().collection("items").document(itemId).set(itemData).get();
            System.out.println("Item saved successfully to Firestore");
            
            // Create and return the item object
            Item item = new Item();
            item.setId(itemId);
            item.setName(name);
            item.setDescription(description);
            item.setCondition(condition);
            item.setStatus("Available");
            item.setImagePath(imageUrl);
            item.setCreatedAt(LocalDateTime.now().toString());
            
            return item;
        } catch (Exception e) {
            System.err.println("Error in saveItem: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save item: " + e.getMessage());
        }
    }

    public List<Item> getAllItems() {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection("items").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            List<Item> items = new ArrayList<>();
            
            for (QueryDocumentSnapshot document : documents) {
                Item item = new Item();
                item.setId(document.getId());
                item.setName(document.getString("name"));
                item.setDescription(document.getString("description"));
                item.setCondition(document.getString("condition"));
                item.setStatus(document.getString("status"));
                item.setImagePath(document.getString("imagePath"));
                item.setCreatedAt(document.getString("createdAt"));
                
                items.add(item);
            }
            return items;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch items from Firestore", e);
        }
    }

    public Item getItemById(String id) {
        try {
            DocumentReference docRef = firestore.collection("items").document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                Item item = new Item();
                item.setId(document.getId());
                item.setName(document.getString("name"));
                item.setDescription(document.getString("description"));
                item.setCondition(document.getString("condition"));
                item.setStatus(document.getString("status"));
                item.setImagePath(document.getString("imagePath"));
                item.setCreatedAt(document.getString("createdAt"));
                
                return item;
            } else {
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to fetch item from Firestore", e);
        }
    }
}