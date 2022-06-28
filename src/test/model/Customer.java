// Generated with g9 DBmasker.

package model;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        @Data
        @Entity
        public class Customer {
            @Id
            @GeneratedValue
            private Integer ID;
            private String email;
            private String name;
            private Date created;
            private Integer addressNo;               

        }

