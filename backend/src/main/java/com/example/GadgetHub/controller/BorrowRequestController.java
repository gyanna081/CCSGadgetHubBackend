package com.example.GadgetHub.controller;

import com.example.GadgetHub.model.BorrowRequest;
import com.example.GadgetHub.service.BorrowRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/requests")  // Changed from "/api/borrow/requests" to match frontend
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class BorrowRequestController {

    private final BorrowRequestService borrowRequestService;

    public BorrowRequestController(BorrowRequestService borrowRequestService) {
        this.borrowRequestService = borrowRequestService;
    }

    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody BorrowRequest request) throws ExecutionException, InterruptedException {
        BorrowRequest createdRequest = borrowRequestService.createRequest(request);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Borrow request submitted successfully",
            "request", createdRequest
        ));
    }

    @GetMapping
    public ResponseEntity<?> getAllRequests(@RequestParam(required = false) String status) throws ExecutionException, InterruptedException {
        List<BorrowRequest> requests;
        
        if (status != null && !status.equalsIgnoreCase("All")) {
            requests = borrowRequestService.getRequestsByStatus(status);
        } else {
            requests = borrowRequestService.getAllRequests();
        }
        
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getRequestById(@PathVariable String id) throws ExecutionException, InterruptedException {
        BorrowRequest request = borrowRequestService.getRequestById(id);
        
        if (request == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(request);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserRequests(@PathVariable String userId) throws ExecutionException, InterruptedException {
        List<BorrowRequest> userRequests = borrowRequestService.getRequestsByBorrowerId(userId);
        return ResponseEntity.ok(userRequests);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRequest(@PathVariable String id, @RequestBody Map<String, String> updateData) throws ExecutionException, InterruptedException {
        String status = updateData.get("status");
        
        if (status == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Status field is required"
            ));
        }
        
        try {
            BorrowRequest updatedRequest = borrowRequestService.updateStatus(id, status);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Request status updated successfully",
                "request", updatedRequest
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRequest(@PathVariable String id) throws ExecutionException, InterruptedException {
        boolean deleted = borrowRequestService.deleteRequest(id);
        
        if (deleted) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Request deleted successfully"
            ));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}