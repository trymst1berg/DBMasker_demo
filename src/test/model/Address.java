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
        public class Address {
            @Id
            @GeneratedValue
            private Integer addressNo;
            private String homeAddress;
            private String postalCode;
            private Date created;               

        }

