package thewolf.sales;

import java.net.URI;

import javax.inject.Inject;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/salesorders")
public class SalesRestService {

  @Inject
  private SalesService salesService;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<SalesOrder> get(@PathVariable int id) {
    SalesOrder salesOrder = salesService.getOrder(id);
    if (salesOrder != null) {
      return new ResponseEntity<SalesOrder>(salesOrder, HttpStatus.OK);
    } else {
      return new ResponseEntity<SalesOrder>(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<?> post(UriComponentsBuilder cmpBuilder, @RequestBody SalesOrder salesOrder) {
    try {
      SalesOrder addedOrder = salesService.addOrder(salesOrder);
      URI location = cmpBuilder.path("/salesorders/{id}").build().expand(addedOrder.getId()).toUri();
      HttpHeaders headers = new HttpHeaders();
      headers.setLocation(location);
      return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
