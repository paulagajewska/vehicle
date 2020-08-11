package com.udacity.vehicles.service;

import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
public class CarServiceUT {
    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarService carService;

    @Test
    public void findAll() {
        //given
        List<Car> cars = new ArrayList<>();
        cars.add(getCar());
        cars.add(getCar());
        when(carRepository.findAll()).thenReturn(cars);

        //when
        List<Car> list = carService.list();

        //then
        assertTrue(list.size() == 2);
    }

    @Test
    public void findById() {
        //given
        Car car = getCar();
        when(carRepository.findById(any())).thenReturn(Optional.of(car));

        //when
        Car carById = carService.findById(1l);

        //then
        assertTrue(car.equals(carById));
    }

    @Test
    public void throwExceptionUsingFindById() {
        //given
        Car car = getCar();
        when(carRepository.findById(any())).thenReturn(Optional.empty());

        //when
        Exception exception = assertThrows(CarNotFoundException.class, () -> {
            carService.findById(1l);
        });

        String expectedMessage = "Car with id 1 wasn't found";
        String actualMessage = exception.getMessage();

        //then
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void throwExceptionUsingSave() {
        //given
        Car car = getCar();
        car.setId(1l);
        when(carRepository.findById(any())).thenReturn(Optional.empty());

        //expected
        assertThrows(CarNotFoundException.class, () -> {
            carService.save(car);
        });
    }

    @Test
    public void save() {
        //given
        Car car = getCar();
        car.setId(1l);
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(carRepository.save(any(Car.class))).thenReturn(car);

        //when
        Car saveCar = carService.save(car);

        //then
        assertTrue(saveCar.equals(car));
        verify(carRepository, times(1)).findById(1l);
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    public void throwExceptionUsingDelete() {
        //given
        Car car = getCar();
        car.setId(1l);
        when(carRepository.findById(any())).thenReturn(Optional.empty());

        //when
        Exception exception = assertThrows(CarNotFoundException.class, () -> {
            carService.delete(car.getId());
        });

        String expectedMessage = "Car with id 1 wasn't found";
        String actualMessage = exception.getMessage();

        //then
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void delete() {
        //given
        Car car = getCar();
        car.setId(1l);
        when(carRepository.findById(any())).thenReturn(Optional.of(car));

        //when
        carService.delete(car.getId());

        //then
        verify(carRepository, times(1)).delete(car);
        verify(carRepository, times(1)).findById(car.getId());
    }

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