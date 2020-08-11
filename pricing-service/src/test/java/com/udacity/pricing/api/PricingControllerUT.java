package com.udacity.pricing.api;

import com.google.gson.Gson;
import com.udacity.pricing.domain.price.Price;
import com.udacity.pricing.service.PricingService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(PricingController.class)
public class PricingControllerUT {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PricingService pricingService;

    @Test
    public void getPrice() throws Exception {
        ResultActions result = mockMvc.perform(get("/services/price").param("vehicleId", "1"));

        MockHttpServletResponse response = result.andReturn().getResponse();
        Assert.assertTrue("Response status is incorrect", response.getStatus() == 200);

        Price priceDto = new Gson().fromJson(response.getContentAsString(), Price.class);

        Assert.assertTrue("Price currency is incorrect", priceDto.getCurrency().equals("USD"));
        Assert.assertTrue("Vehicle ID is incorrect", priceDto.getVehicleId().equals(1L));
        Assert.assertTrue("Price is incorrect, because price is less or equal zero",
                priceDto.getPrice().compareTo(new BigDecimal(0)) == 1);
    }

}