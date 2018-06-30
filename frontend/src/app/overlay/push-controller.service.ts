import { Injectable, InjectionToken, Injector } from "@angular/core";
import { ComponentPortal, ComponentType, PortalInjector } from "@angular/cdk/portal";
import { Overlay, OverlayConfig, OverlayRef } from "@angular/cdk/overlay";
import { PushedControllerOverlayComponent } from "./pushed-controller-overlay.component";

export const PUSHED_CONTROLLER_DATA = new InjectionToken<any>("PUSHED_CONTROLLER_DATA");

@Injectable()
export class PushControllerService {

  /**
   * Create the service via the injected overlay and a global injector
   *
   * @param {Overlay} overlay the injected overlay.
   * @param {Injector} injector the injected global injector.
   */
  constructor(private overlay: Overlay, private injector: Injector) { }

  /**
   * Open the global controller overlay.
   *
   * @param {ComponentType<T>} componentType the component to open.
   * @param {any} data the data to be injected into the controller.
   * @returns {PushedControllerOverlayRef} a handle to close the overlay.
   */
  open<T extends PushedControllerOverlayComponent>(componentType: ComponentType<T>, data: any = null): void {
    // For display config
    const positionStrategy = this.overlay.position()
      .global().right("0px").top("0px").width("100%").height("100%");
    const overlayConfig = new OverlayConfig({
      hasBackdrop: true,
      backdropClass: "cdk-overlay-dark-backdrop",
      panelClass: "",
      scrollStrategy: this.overlay.scrollStrategies.block(),
      positionStrategy
    });
    const overlayRef = this.overlay.create(overlayConfig);
    // For data injection
    const injectionTokens = new WeakMap();
    injectionTokens.set(PushedControllerOverlayRef, new PushedControllerOverlayRef(overlayRef));
    injectionTokens.set(PUSHED_CONTROLLER_DATA, data);
    const injector = new PortalInjector(this.injector, injectionTokens);
    const loadingPortal = new ComponentPortal(componentType, null, injector);
    overlayRef.attach(loadingPortal);
  }

}

/**
 * Acts as a handle for the global loading overlay.
 * It facades the complicated overlay ref from angular. Only necessary methods are provided.
 */
export class PushedControllerOverlayRef {

  /**
   * Wrap the overlay ref from angular.
   *
   * @param {OverlayRef} overlayRef the overlay ref from angular.
   */
  constructor(private overlayRef: OverlayRef) { }

  /**
   * Close the global controller overlay.
   */
  close(): void {
    this.overlayRef.dispose();
  }

}
