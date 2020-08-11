package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository repository;
    private final WebClient webClientMaps;
    private final WebClient webClientPricing;
    private final ModelMapper modelMapper;

    public CarService(CarRepository repository,
                      @Qualifier("maps") WebClient webClientMaps,
                      @Qualifier("pricing") WebClient webClientPricing,
                      ModelMapper modelMapper) {
        this.repository = repository;
        this.webClientMaps = webClientMaps;
        this.webClientPricing = webClientPricing;
        this.modelMapper = modelMapper;
    }

    /**
     * Gathers a list of all vehicles
     *
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        return repository.findAll();
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     *
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {
        Car car = null;

        Optional<Car> optionalCar = repository.findById(id);

        if (optionalCar.isPresent()) {
            car = optionalCar.get();
        } else {
            throw new CarNotFoundException(String.format("Car with id %d wasn't found", id));
        }

        String price = getPrice(id);
        car.setPrice(price);

        Location location = getAddress(car.getLocation());
        car.setLocation(location);

        return car;
    }

    private String getPrice(Long id) {
        PriceClient priceClient = new PriceClient(webClientPricing);
        return priceClient.getPrice(id);
    }

    private Location getAddress(Location location) {
        MapsClient mapsClient = new MapsClient(webClientMaps, modelMapper);
        return mapsClient.getAddress(location);
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     *
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return repository.save(car);
    }

    /**
     * Deletes a given car by ID
     *
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        Optional<Car> optionalCar = repository.findById(id);

        if (optionalCar.isPresent()) {
            Car car = optionalCar.get();

            repository.delete(car);
        } else {
            throw new CarNotFoundException(String.format("Car with id %d wasn't found", id));
        }
    }
}
