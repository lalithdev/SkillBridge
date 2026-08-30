package com.skillbridge.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

public class BCryptGeneratorTest {

    @Test
    void generateHashes() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Map<String, String> passwords = new LinkedHashMap<>();
        
        // Admin
        passwords.put("admin@skillbridge.org", "Admin@SkillBridge2026!");
        
        // Colleges
        passwords.put("dean.placement@iith.ac.in", "IITH@Placement2026");
        passwords.put("placements@iitm.ac.in", "IITM@Placement2026");
        passwords.put("tpo@nitw.ac.in", "NITW@Placement2026");
        passwords.put("placement.cell@iiit.ac.in", "IIITH@Placement2026");
        passwords.put("pat@vit.ac.in", "VIT@Placement2026");
        
        // Companies
        passwords.put("campus.recruitment@microsoft.com", "MSFT@Hire2026!");
        passwords.put("university.hiring@google.com", "GOOG@Hire2026!");
        passwords.put("student.programs@amazon.com", "AMZN@Hire2026!");
        passwords.put("campus.talent@deloitte.com", "DELO@Hire2026!");
        passwords.put("entrylevel.hiring@accenture.com", "ACCN@Hire2026!");
        passwords.put("university.relations@ibm.com", "IBMC@Hire2026!");
        passwords.put("campus.recruiting@oracle.com", "ORCL@Hire2026!");
        
        // Students
        passwords.put("vangnaini.k@iith.ac.in", "Skill@Vangnaini2026");
        passwords.put("lalith.aditya@iitm.ac.in", "Skill@Lalith2026");
        passwords.put("james.maddi@nitw.ac.in", "Skill@James2026");
        passwords.put("prajwal.ar@iiit.ac.in", "Skill@Prajwal2026");
        passwords.put("sai.janaki@vit.ac.in", "Skill@Janaki2026");
        passwords.put("thavitha.manne@iith.ac.in", "Skill@Thavitha2026");

        for (Map.Entry<String, String> entry : passwords.entrySet()) {
            String hash = encoder.encode(entry.getValue());
            System.out.println("CRED_MAP: " + entry.getKey() + " | " + entry.getValue() + " | " + hash);
        }
    }
}
