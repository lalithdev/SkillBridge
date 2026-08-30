export function mockDelay(data, ms = 350) {
  return new Promise((resolve) => {
    setTimeout(() => resolve(data), ms);
  });
}

export function mockReject(message, ms = 350) {
  return new Promise((_, reject) => {
    setTimeout(() => reject({ status: 400, message, fieldErrors: {} }), ms);
  });
}
