import CoreLocation
import SwiftUI
import Shared

final class LocationService: NSObject, ObservableObject, CLLocationManagerDelegate {
    let state = LocationState()
    private let manager = CLLocationManager()
    private var pending = false
    private var timeout: Timer?

    override init() {
        super.init()
        manager.delegate = self
        manager.desiredAccuracy = kCLLocationAccuracyKilometer
    }

    func locate() {
        timeout?.invalidate()
        pending = true
        state.start()
        switch manager.authorizationStatus {
        case .notDetermined:
            manager.requestWhenInUseAuthorization()
        case .authorizedAlways, .authorizedWhenInUse:
            requestPosition()
        default:
            fail("Разреши доступ к геопозиции в настройках приложения, чтобы узнать погоду рядом.")
        }
    }

    private func requestPosition() {
        manager.requestLocation()
        timeout?.invalidate()
        timeout = Timer.scheduledTimer(withTimeInterval: 25, repeats: false) { [weak self] _ in
            self?.fail("Не удалось определить местоположение. Проверь геолокацию и попробуй ещё раз.")
        }
    }

    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        guard pending else { return }
        switch manager.authorizationStatus {
        case .authorizedAlways, .authorizedWhenInUse:
            requestPosition()
        case .denied, .restricted:
            fail("Разреши доступ к геопозиции в настройках приложения, чтобы узнать погоду рядом.")
        default:
            break
        }
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard pending, let location = locations.last, location.horizontalAccuracy >= 0 else { return }
        pending = false
        timeout?.invalidate()
        state.update(latitude: location.coordinate.latitude, longitude: location.coordinate.longitude)
    }

    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        guard pending else { return }
        fail("Местоположение сейчас недоступно. Проверь геолокацию и попробуй ещё раз.")
    }

    private func fail(_ message: String) {
        pending = false
        timeout?.invalidate()
        manager.stopUpdatingLocation()
        state.fail(message: message)
    }
}

struct ComposeView: UIViewControllerRepresentable {
    let location: LocationService

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(location: location.state, onLocate: { location.locate() })
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    @StateObject private var location = LocationService()

    var body: some View {
        ComposeView(location: location)
            .ignoresSafeArea()
    }
}
