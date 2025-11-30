export interface ZeroWorldReservationRequest {
  themeId: string;
  reservationDate: string;
  reservationTime: string;
  name: string;
  phone: string;
  people: number;
  paymentType: string;
  policy: boolean;
}

export interface EarthEscapeReservationRequest {
  branch: string;
  theme: string;
  date: string;
  time: string;
  name: string;
  phone: string;
  people: string;
  paymentMethod: string;
  policy: boolean;
}

export interface ReservationResponse {
  success: boolean;
  message: string;
  reservationId: string | null;
}

interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string | null;
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const createZeroWorldReservation = async (
  request: ZeroWorldReservationRequest
): Promise<ReservationResponse> => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/reservations/zero-world`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });

    const result: ApiResponse<ReservationResponse> = await response.json();

    // 응답이 성공이 아니면 에러로 처리
    if (!result.success) {
      return {
        success: false,
        message: result.message || '예약에 실패했습니다.',
        reservationId: null,
      };
    }

    return result.data;
  } catch (error) {
    console.error('Error creating Zero World reservation:', error);
    throw error;
  }
};

export const createEarthEscapeReservation = async (
  request: EarthEscapeReservationRequest
): Promise<ReservationResponse> => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/reservations/earth-escape`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });

    const result: ApiResponse<ReservationResponse> = await response.json();

    // 응답이 성공이 아니면 에러로 처리
    if (!result.success) {
      return {
        success: false,
        message: result.message || '예약에 실패했습니다.',
        reservationId: null,
      };
    }

    return result.data;
  } catch (error) {
    console.error('Error creating Earth Escape reservation:', error);
    throw error;
  }
};
