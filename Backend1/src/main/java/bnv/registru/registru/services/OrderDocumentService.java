package bnv.registru.registru.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface OrderDocumentService {
    ByteArrayOutputStream generateOrderDocument(Long orderId, String registrationDate, String recipient) throws IOException;
}
