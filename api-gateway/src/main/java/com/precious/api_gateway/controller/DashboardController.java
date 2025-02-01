package com.precious.api_gateway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class DashboardController {

    @GetMapping("/client")
    public String clientDashboard() {
        return "dashboard/client/index";  
    }

    @GetMapping("/user")
    public String userDashboard() {
        return "dashboard/user/index";  
    }

    @GetMapping("/admin")
    public String adminDashboard() {
        return "dashboard/admin/index";  
    }

    @GetMapping("/client/task-history")
    public String taskHistory() {
        return "dashboard/client/task-history/task-history"; 
    }

    @GetMapping("/client/transaction-history")
    public String transactionHistory() {
        return "dashboard/client/transaction-history/transaction-history"; 
    }

    @GetMapping("/task-details")
    public String taskDetails() {
        return "dashboard/client/task-history/task-details"; 
    }

    @GetMapping("/faq")
    public String faq() {
        return "/dashboard/client/faq"; 
    }

    @GetMapping("/help")
    public String help() {
        return "dashboard/client/help"; 
    }
}
