package com.merchant.service;

import com.merchant.entity.Merchant;
import com.merchant.entity.MerchantStatus;
import com.merchant.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MerchantService {
    @Autowired
    private MerchantRepository merchantRepository;

    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }

    public List<Merchant> getMerchantsByStatus(MerchantStatus status) {
        return merchantRepository.findByStatus(status);
    }

    @Transactional
    public Merchant updateMerchantStatus(Long id, MerchantStatus status) {
        Merchant merchant = merchantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Merchant not found"));
        merchant.setStatus(status);
        return merchantRepository.save(merchant);
    }

    public Map<MerchantStatus, Long> getMerchantStatusStatistics() {
        return Map.of(
            MerchantStatus.PENDING, merchantRepository.countByStatus(MerchantStatus.PENDING),
            MerchantStatus.APPROVED, merchantRepository.countByStatus(MerchantStatus.APPROVED),
            MerchantStatus.REJECTED, merchantRepository.countByStatus(MerchantStatus.REJECTED),
            MerchantStatus.DISABLED, merchantRepository.countByStatus(MerchantStatus.DISABLED)
        );
    }
}
