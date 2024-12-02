package com.moa.moapay.domain.code.service;

import com.moa.moapay.domain.code.model.dto.*;

public interface CodeService {
    GetQRCodeResponseDto getQRCode(GetQRCodeRequestDto dto);
    GetQRInfoResponseDto getQRInfo(String QRCode);
    void disableQRCode(String QRCode);
    GetBarcodeResponseDto getBarcode(GetBarcodeRequestDto dto);
    GetBarcodeInfoResponseDto getBarcodeInfo(String barcode);
}
