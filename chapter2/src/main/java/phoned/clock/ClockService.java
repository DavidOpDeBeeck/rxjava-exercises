package phoned.clock;

import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.Thread.sleep;

public class ClockService {

    private static final int THREADS = 1;

    private Clock clock;
    private int tickInterval;
    private ExecutorService executorService;

    public ClockService(Clock clock, int tickInterval) {
        this.clock = clock;
        this.tickInterval = tickInterval;
        this.executorService = Executors.newFixedThreadPool(THREADS);
    }

    public void init() {
    }

    public Observable<LocalDateTime> getTime() {
        return Observable.create(subscriber -> {
            Future<?> submit = executorService.submit(() -> notifySubscriberEveryInterval(subscriber));
            subscriber.add(Subscriptions.create(() -> submit.cancel(true)));
        });
    }

    private void notifySubscriberEveryInterval(Subscriber<? super LocalDateTime> subscriber) {
        try {
            while (true) {
                notifySubscriber(subscriber);
                sleep(tickInterval);
            }
        } catch (Exception e) {
            subscriber.onError(e);
        }
    }

    private void notifySubscriber(Subscriber<? super LocalDateTime> subscriber) {
        subscriber.onNext(LocalDateTime.now(clock));
    }
}
