package edu.valle.modules.supplier.config;

import edu.valle.modules.supplier.soap.dto.GetSupplierProductBySkuRequest;
import edu.valle.modules.supplier.soap.dto.GetSupplierProductBySkuResponse;
import edu.valle.modules.supplier.soap.dto.ReplenishSupplierProductRequest;
import edu.valle.modules.supplier.soap.dto.ReplenishSupplierProductResponse;
import edu.valle.modules.supplier.soap.dto.RestoreSupplierProductStockRequest;
import edu.valle.modules.supplier.soap.dto.RestoreSupplierProductStockResponse;
import edu.valle.modules.supplier.soap.dto.SoapNamespaces;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class SupplierSoapConfig {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
            ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "supplierProducts")
    public DefaultWsdl11Definition supplierProductsWsdl(XsdSchema supplierProductsSchema) {
        DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
        definition.setPortTypeName("SupplierProductsPort");
        definition.setLocationUri("/ws");
        definition.setTargetNamespace(SoapNamespaces.SUPPLIER);
        definition.setSchema(supplierProductsSchema);
        return definition;
    }

    @Bean
    public XsdSchema supplierProductsSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/supplier-products.xsd"));
    }

    @Bean
    public Jaxb2Marshaller supplierMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(
                GetSupplierProductBySkuRequest.class,
                GetSupplierProductBySkuResponse.class,
                ReplenishSupplierProductRequest.class,
                ReplenishSupplierProductResponse.class,
                RestoreSupplierProductStockRequest.class,
                RestoreSupplierProductStockResponse.class
        );
        return marshaller;
    }

    @Bean
    public WebServiceTemplate supplierWebServiceTemplate(
            Jaxb2Marshaller supplierMarshaller,
            @Value("${supplier.soap.default-uri}") String defaultUri) {
        WebServiceTemplate template = new WebServiceTemplate();
        template.setMarshaller(supplierMarshaller);
        template.setUnmarshaller(supplierMarshaller);
        template.setDefaultUri(defaultUri);
        return template;
    }
}
