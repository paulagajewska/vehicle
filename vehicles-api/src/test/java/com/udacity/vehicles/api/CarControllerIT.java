package com.udacity.vehicles.api;

import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.net.URI;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Implements testing of the CarController class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarService carService;

    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @Before
    public void setup() {
        Car car = getCar();
        car.setId(1L);
        given(carService.save(any())).willReturn(car);
        given(carService.findById(any())).willReturn(car);
        given(carService.list()).willReturn(Collections.singletonList(car));
    }

    /**
     * Tests for successful creation of new car in the system
     *
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void createCar() throws Exception {
        //given
        Car car = getCar();
        car.setId(1l);

        //when
        ResultActions resultActions = mvc.perform(
                post(new URI("/cars"))
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated());

        //then
        resultActions.andExpect(jsonPath("$.id", is(car.getId().intValue())))
                .andExpect(jsonPath("$.condition", is(car.getCondition().name())))
                .andExpect(jsonPath("$.details.body", is(car.getDetails().getBody())))
                .andExpect(jsonPath("$.details.model", is(car.getDetails().getModel())))
                .andExpect(jsonPath("$.details.manufacturer.code", is(car.getDetails().getManufacturer().getCode())))
                .andExpect(jsonPath("$.details.manufacturer.name", is(car.getDetails().getManufacturer().getName())))
                .andExpect(jsonPath("$.details.numberOfDoors", is(car.getDetails().getNumberOfDoors())))
                .andExpect(jsonPath("$.details.fuelType", is(car.getDetails().getFuelType())))
                .andExpect(jsonPath("$.details.engine", is(car.getDetails().getEngine())))
                .andExpect(jsonPath("$.details.mileage", is(car.getDetails().getMileage())))
                .andExpect(jsonPath("$.details.modelYear", is(car.getDetails().getModelYear())))
                .andExpect(jsonPath("$.details.productionYear", is(car.getDetails().getProductionYear())))
                .andExpect(jsonPath("$.details.externalColor", is(car.getDetails().getExternalColor())))
                .andExpect(jsonPath("$.location.lat", is(car.getLocation().getLat())))
                .andExpect(jsonPath("$.location.lon", is(car.getLocation().getLon())));
    }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     *
     * @throws Exception if the read operation of the vehicle list fails
     */
    @Test
    public void listCars() throws Exception {
        //given
        Car car = getCar();
        car.setId(1l);

        //when
        ResultActions resultActions = mvc.perform(
                get(new URI("/cars"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        //then
        resultActions.andExpect(jsonPath("$._embedded.carList.[0].id", is(car.getId().intValue())))
                .andExpect(jsonPath("$._embedded.carList.[0].condition", is(car.getCondition().name())))
                .andExpect(jsonPath("$._embedded.carList.[0].details.body", is(car.getDetails().getBody())))
                .andExpect(jsonPath("$._embedded.carList.[0].details.model", is(car.getDetails().getModel())))
                .andExpect(jsonPath("$._embedded.carList.[0].details.manufacturer.code", is(car.getDetails().getManufacturer().getCode())))
                .andExpect(jsonPath("$._embedded.carList.[0].details.manufacturer.name", is(car.getDetails().getManufacturer().getName())))
                .andExpect(jsonPath("$._embedded.carList.[0].details.numberOfDoors", is(car.getDetails().getNumberOfDoors())))
                .andExpect(jsonPath("$._embedded.carList.[0].details.fuelType", is(car.getDetails().getFuelType())))
                .andExpect(jsonPath("$._embedded.carList.[0].details.engine", is(car.getDetails().getEngine())))
                .andExpect(jsonPath("$._embedded.carList.[0].details.mileage", is(car.getDetails().getMileage())))
                .andExpect(jsonPath("$._embedded.carList.[0].details.modelYear", is(car.getDetails().getModelYear())))
                .andExpect(jsonPath("$._embedded.carList.[0].details.productionYear", is(car.getDetails().getProductionYear())))
                .andExpect(jsonPath("$._embedded.carList.[0].details.externalColor", is(car.getDetails().getExternalColor())))
                .andExpect(jsonPath("$._embedded.carList.[0].location.lat", is(car.getLocation().getLat())))
                .andExpect(jsonPath("$._embedded.carList.[0].location.lon", is(car.getLocation().getLon())));

        verify(carService, times(1)).list();
    }

    /**
     * Tests the read operation for a single car by ID.
     *
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    public void findCar() throws Exception {
        //given:
        Car car = getCar();
        car.setId(1l);

        //when
        ResultActions resultActions = mvc.perform(
                get(new URI("/cars/1"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isOk());

        //then
        resultActions.andExpect(jsonPath("$.id", is(car.getId().intValue())))
                .andExpect(jsonPath("$.condition", is(car.getCondition().name())))
                .andExpect(jsonPath("$.details.body", is(car.getDetails().getBody())))
                .andExpect(jsonPath("$.details.model", is(car.getDetails().getModel())))
                .andExpect(jsonPath("$.details.manufacturer.code", is(car.getDetails().getManufacturer().getCode())))
                .andExpect(jsonPath("$.details.manufacturer.name", is(car.getDetails().getManufacturer().getName())))
                .andExpect(jsonPath("$.details.numberOfDoors", is(car.getDetails().getNumberOfDoors())))
                .andExpect(jsonPath("$.details.fuelType", is(car.getDetails().getFuelType())))
                .andExpect(jsonPath("$.details.engine", is(car.getDetails().getEngine())))
                .andExpect(jsonPath("$.details.mileage", is(car.getDetails().getMileage())))
                .andExpect(jsonPath("$.details.modelYear", is(car.getDetails().getModelYear())))
                .andExpect(jsonPath("$.details.productionYear", is(car.getDetails().getProductionYear())))
                .andExpect(jsonPath("$.details.externalColor", is(car.getDetails().getExternalColor())))
                .andExpect(jsonPath("$.location.lat", is(car.getLocation().getLat())))
                .andExpect(jsonPath("$.location.lon", is(car.getLocation().getLon())));

        verify(carService, times(1)).findById(1l);
    }

    /**
     * Tests the deletion of a single car by ID.
     *
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void deleteCar() throws Exception {
        //when
        MvcResult result = mvc.perform(
                delete(new URI("/cars/1"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8)).andReturn();


        //then
        MockHttpServletResponse response = result.getResponse();
        Assert.assertTrue("Response status is incorrect", response.getStatus() == HttpStatus.NO_CONTENT.value());

        verify(carService, times(1)).delete(1l);
    }

    /**
     * Creates an example Car object for use in testing.
     *
     * @return an example Car object
     */
    private Car getCar() {
        Car car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        return car;
    }
}