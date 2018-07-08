/**
 * Run [f] after a short delay.
 *
 * @param {() => void} f the function to run after the short delay.
 */
export function shortDelay(f: () => void) {
  setTimeout(f, 50);
}
