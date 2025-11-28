interface Theme {
  themeId: number;
  themeName: string;
  cafeName: string;
  branchName: string;
}

interface UserAlert {
  alertId: number;
  isActive: boolean;
  theme: Theme;
  dateStart: string;
  dateEnd: string;
  preferredTimes: string[];
  preferredDays: string[];
  numPeople: number;
  notifiedCount: number;
  lastNotifiedAt: string | null;
}

export interface ReservationRequest {
  themeId: string;
  reservationDate: string;
  timeSlot?: string;
  name: string;
  phone: string;
  people: number;
  paymentType: string;
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

export const getUserAlerts = async (): Promise<UserAlert[]> => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/user-alerts`);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const result: ApiResponse<UserAlert[]> = await response.json();

    if (!result.success) {
      throw new Error(result.message || 'Failed to fetch user alerts');
    }

    return result.data;
  } catch (error) {
    console.error('Error fetching user alerts:', error);
    throw error;
  }
};

export const createReservation = async (request: ReservationRequest): Promise<ReservationResponse> => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/reservations`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const result: ApiResponse<ReservationResponse> = await response.json();

    if (!result.success) {
      throw new Error(result.message || 'Failed to create reservation');
    }

    return result.data;
  } catch (error) {
    console.error('Error creating reservation:', error);
    throw error;
  }
};
