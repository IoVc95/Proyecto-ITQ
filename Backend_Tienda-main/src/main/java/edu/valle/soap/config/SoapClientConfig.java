package edu.valle.soap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

@Configuration
public class SoapClientConfig {

    @Bean
    public Jaxb2Marshaller supplierMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("edu.valle.soap.supplier.dto");
        return marshaller;
    }

    @Bean
    public WebServiceTemplate supplierWebServiceTemplate(Jaxb2Marshaller supplierMarshaller) {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(supplierMarshaller);
        webServiceTemplate.setUnmarshaller(supplierMarshaller);
        webServiceTemplate.setDefaultUri("http://localhost:8080/ws");
        return webServiceTemplate;
    }
}
