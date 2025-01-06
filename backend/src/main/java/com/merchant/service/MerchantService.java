package com.merchant.service;

import com.merchant.entity.Merchant;
import com.merchant.entity.MerchantStatus;
import com.merchant.repository.MerchantRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MerchantService {
    @Autowired
    private MerchantRepository merchantRepository;

    public Page<Merchant> getAllMerchants(int page, int size, String name, String businessType, MerchantStatus status) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Specification<Merchant> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (StringUtils.hasText(name)) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }
            
            if (StringUtils.hasText(businessType)) {
                predicates.add(criteriaBuilder.equal(root.get("businessType"), businessType));
            }
            
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        return merchantRepository.findAll(spec, pageable);
    }

    public List<Merchant> getMerchantsByStatus(MerchantStatus status) {
        return merchantRepository.findByStatus(status);
    }

    public Merchant getMerchantById(Long id) {
        return merchantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("商家不存在"));
    }

    @Transactional
    public Merchant updateMerchantStatus(Long id, MerchantStatus status, String rejectReason) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("商家不存在"));

        if (status == MerchantStatus.REJECTED && (rejectReason == null || rejectReason.trim().isEmpty())) {
            throw new IllegalArgumentException("拒绝时必须提供拒绝原因");
        }

        merchant.setStatus(status);
        if (status == MerchantStatus.REJECTED) {
            merchant.setRejectReason(rejectReason);
        } else {
            merchant.setRejectReason(null);
        }

        return merchantRepository.save(merchant);
    }

    public Map<MerchantStatus, Long> getMerchantStatusStatistics() {
        List<Merchant> merchants = merchantRepository.findAll();
        return merchants.stream()
                .collect(Collectors.groupingBy(Merchant::getStatus, Collectors.counting()));
    }
}
