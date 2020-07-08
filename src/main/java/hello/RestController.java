package hello;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    private Scheduler scheduler = Schedulers.newParallel("Prashant");
    private Scheduler scheduler2 = Schedulers.newParallel("Priyanka");

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        DBOperation();
        return new ResponseEntity<>("Hello I'm Done", HttpStatus.OK);
    }

    private Flux<Integer> DBOperation() {
        ArrayList<Integer> dataList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            dataList.add(i);
        }
        Flux<Integer> flux = Flux.fromIterable(dataList).concatWith(Flux.error(new RuntimeException("My Exception"))).concatWith(Flux.just(6));
        flux.doOnNext(event -> {
            System.out.println("doOnNext:: " + Thread.currentThread().getName());
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(event.intValue());
        }).doOnError(error -> System.out.println("doOnError :::" + Thread.currentThread().getName()))
                .doOnComplete(() -> System.out.println("doOnComplete :::" + Thread.currentThread().getName()))
                .subscribeOn(scheduler)
                .publishOn(scheduler2)
                .subscribe(v -> System.out.println("subscribe :::" + Thread.currentThread().getName() + " value == " + v.intValue()), e -> {
                    System.out.println("OnException :::" + Thread.currentThread().getName());
                });
        flux.doOnTerminate(() -> System.out.println("doOnTerminate :::" + Thread.currentThread().getName()));
        return flux;
    }


}
