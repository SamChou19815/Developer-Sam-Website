/**
 * Run [f] after a short delay.
 *
 * @param {() => void} f the function to run after the short delay.
 */
export function shortDelay(f: () => void) {
  setTimeout(f, 50);
}

/**
 * An array of all possible hours.
 * @type {number[]}
 */
export const possibleHoursArray = Array<number>(24);

for (let i = 0; i < 24; i++) {
  possibleHoursArray[i] = i;
}
