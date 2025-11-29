import { FC, useState } from 'react';
import { createZeroWorldReservation } from '../services/reservationApi';
import './ZeroWorldReservation.css';

interface ReservationForm {
  themeId: string;
  reservationDate: string;
  reservationTime: string;
  name: string;
  phone: string;
  people: number;
  paymentType: string;
  policy: boolean;
}

const ZeroWorldReservation: FC = () => {
  const [form, setForm] = useState<ReservationForm>({
    themeId: '',
    reservationDate: '',
    reservationTime: '',
    name: '',
    phone: '',
    people: 2,
    paymentType: '1',
    policy: false,
  });

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  // 주중/주말 판단 (금요일 17시 이후는 주말)
  const isWeekend = (date: string, time: string): boolean => {
    if (!date || !time) return false;

    const selectedDate = new Date(date + 'T' + time);
    const dayOfWeek = selectedDate.getDay();
    const hour = selectedDate.getHours();

    // 토요일(6) 또는 일요일(0)
    if (dayOfWeek === 0 || dayOfWeek === 6) return true;

    // 금요일(5) 17시 이후
    if (dayOfWeek === 5 && hour >= 17) return true;

    return false;
  };

  const themes = {
    weekday: [
      { id: '51', name: 'ALIVE' },
      { id: '54', name: '사랑하는감' },
      { id: '56', name: '깜방탈출' },
      { id: '58', name: 'NOX' },
      { id: '50', name: '층간소음' },
    ],
    weekend: [
      { id: '52', name: 'ALIVE' },
      { id: '55', name: '사랑하는감' },
      { id: '57', name: '깜방탈출' },
      { id: '59', name: 'NOX' },
      { id: '51', name: '층간소음' },
    ],
  };

  const availableTimes = [
    '11:00:00',
    '12:30:00',
    '14:00:00',
    '15:30:00',
    '17:00:00',
    '18:30:00',
    '20:00:00',
    '21:30:00',
  ];

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!form.policy) {
      setMessage({ type: 'error', text: '개인정보 처리방침에 동의해주세요.' });
      return;
    }

    try {
      setLoading(true);
      setMessage(null);

      const response = await createZeroWorldReservation(form);

      if (response.success) {
        setMessage({ type: 'success', text: '예약이 완료되었습니다!' });
        // 폼 초기화
        setForm({
          themeId: '',
          reservationDate: '',
          reservationTime: '',
          name: '',
          phone: '',
          people: 2,
          paymentType: '1',
          policy: false,
        });
      } else {
        setMessage({ type: 'error', text: response.message || '예약에 실패했습니다.' });
      }
    } catch (error: any) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || '예약 중 오류가 발생했습니다.'
      });
    } finally {
      setLoading(false);
    }
  };

  // 전화번호 자동 하이픈 포맷팅
  const formatPhoneNumber = (value: string): string => {
    // 숫자만 추출
    const numbers = value.replace(/[^0-9]/g, '');

    // 길이에 따라 포맷팅
    if (numbers.length <= 3) {
      return numbers;
    } else if (numbers.length <= 7) {
      return `${numbers.slice(0, 3)}-${numbers.slice(3)}`;
    } else if (numbers.length <= 10) {
      return `${numbers.slice(0, 3)}-${numbers.slice(3, 6)}-${numbers.slice(6)}`;
    } else {
      return `${numbers.slice(0, 3)}-${numbers.slice(3, 7)}-${numbers.slice(7, 11)}`;
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;

    let newValue = value;

    // 전화번호 필드인 경우 자동 포맷팅
    if (name === 'phone') {
      newValue = formatPhoneNumber(value);
    }

    setForm(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? (e.target as HTMLInputElement).checked : newValue,
    }));
  };

  const selectedThemes = isWeekend(form.reservationDate, form.reservationTime)
    ? themes.weekend
    : themes.weekday;

  return (
    <div className="reservation-container">
      <header className="header">
        <h1 className="title">제로월드 홍대점 예약</h1>
      </header>

      <main className="main-content">
        <form className="reservation-form" onSubmit={handleSubmit}>
          <div className="form-section">
            <h2 className="section-title">예약 정보</h2>

            <div className="form-group">
              <label htmlFor="reservationDate">예약 날짜 *</label>
              <input
                type="date"
                id="reservationDate"
                name="reservationDate"
                value={form.reservationDate}
                onChange={handleChange}
                required
                min={new Date().toISOString().split('T')[0]}
              />
            </div>

            <div className="form-group">
              <label htmlFor="reservationTime">예약 시간 *</label>
              <select
                id="reservationTime"
                name="reservationTime"
                value={form.reservationTime}
                onChange={handleChange}
                required
              >
                <option value="">시간을 선택하세요</option>
                {availableTimes.map(time => (
                  <option key={time} value={time}>
                    {time.substring(0, 5)}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="themeId">
                테마 *
                {form.reservationDate && form.reservationTime && (
                  <span className="theme-type">
                    ({isWeekend(form.reservationDate, form.reservationTime) ? '주말' : '주중'})
                  </span>
                )}
              </label>
              <select
                id="themeId"
                name="themeId"
                value={form.themeId}
                onChange={handleChange}
                required
                disabled={!form.reservationDate || !form.reservationTime}
              >
                <option value="">테마를 선택하세요</option>
                {selectedThemes.map(theme => (
                  <option key={theme.id} value={theme.id}>
                    {theme.name}
                  </option>
                ))}
              </select>
              {!form.reservationDate || !form.reservationTime ? (
                <p className="form-hint">날짜와 시간을 먼저 선택해주세요</p>
              ) : null}
            </div>

            <div className="form-group">
              <label htmlFor="people">인원 수 *</label>
              <select
                id="people"
                name="people"
                value={form.people}
                onChange={handleChange}
                required
              >
                <option value={2}>2명</option>
                <option value={3}>3명</option>
                <option value={4}>4명</option>
                <option value={5}>5명</option>
                <option value={6}>6명</option>
              </select>
            </div>
          </div>

          <div className="form-section">
            <h2 className="section-title">예약자 정보</h2>

            <div className="form-group">
              <label htmlFor="name">이름 *</label>
              <input
                type="text"
                id="name"
                name="name"
                value={form.name}
                onChange={handleChange}
                placeholder="홍길동"
                required
                maxLength={10}
              />
            </div>

            <div className="form-group">
              <label htmlFor="phone">연락처 *</label>
              <input
                type="tel"
                id="phone"
                name="phone"
                value={form.phone}
                onChange={handleChange}
                placeholder="01012345678 또는 010-1234-5678"
                required
                maxLength={13}
              />
              <p className="form-hint">숫자만 입력하셔도 자동으로 하이픈이 추가됩니다</p>
            </div>
          </div>

          <div className="form-section">
            <h2 className="section-title">결제 및 동의</h2>

            <div className="form-group">
              <label>결제 방식</label>
              <div className="payment-info">
                현장 결제
              </div>
            </div>

            <div className="form-group checkbox-group">
              <label className="checkbox-label">
                <input
                  type="checkbox"
                  name="policy"
                  checked={form.policy}
                  onChange={handleChange}
                  required
                />
                <span>개인정보 처리방침 및 주의사항에 동의합니다 *</span>
              </label>
            </div>

            <div className="notice-box">
              <h3>주의사항</h3>
              <ul>
                <li>예약해 주신 시간은 입장시간입니다.</li>
                <li>브리핑 및 짐 보관을 위해 15분전에 도착해주셔야합니다.</li>
                <li>늦으실 경우 늦은 시간만큼 시간이 차감됩니다.</li>
                <li>장치를 힘으로 억지로 열거나 작동시키려 할 경우 부상과 파손의 위험이 있습니다.</li>
                <li>소품 및 장치 파손 시 배상의 책임이 있습니다.</li>
              </ul>
            </div>
          </div>

          {message && (
            <div className={`message ${message.type}`}>
              {message.text}
            </div>
          )}

          <div className="form-actions">
            <button
              type="submit"
              className="btn-submit"
              disabled={loading}
            >
              {loading ? '예약 중...' : '예약하기'}
            </button>
          </div>
        </form>
      </main>
    </div>
  );
};

export default ZeroWorldReservation;
